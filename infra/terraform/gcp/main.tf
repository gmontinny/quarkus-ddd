# =============================================================================
# APIs habilitadas
# =============================================================================
resource "google_project_service" "apis" {
  for_each = toset([
    "container.googleapis.com",
    "sqladmin.googleapis.com",
    "artifactregistry.googleapis.com",
    "compute.googleapis.com",
    "servicenetworking.googleapis.com",
  ])
  service            = each.value
  disable_on_destroy = false
}

# =============================================================================
# VPC
# =============================================================================
resource "google_compute_network" "olist" {
  name                    = "${var.cluster_name}-vpc"
  auto_create_subnetworks = false
  depends_on              = [google_project_service.apis]
}

resource "google_compute_subnetwork" "olist" {
  name          = "${var.cluster_name}-subnet"
  ip_cidr_range = "10.0.0.0/20"
  region        = var.region
  network       = google_compute_network.olist.id

  secondary_ip_range {
    range_name    = "pods"
    ip_cidr_range = "10.16.0.0/14"
  }
  secondary_ip_range {
    range_name    = "services"
    ip_cidr_range = "10.20.0.0/18"
  }
}

# =============================================================================
# Artifact Registry
# =============================================================================
resource "google_artifact_registry_repository" "olist" {
  location      = var.region
  repository_id = "olist"
  format        = "DOCKER"
  labels        = var.labels
  depends_on    = [google_project_service.apis]
}

# =============================================================================
# GKE Cluster
# =============================================================================
resource "google_container_cluster" "olist" {
  name     = var.cluster_name
  location = var.region

  network    = google_compute_network.olist.name
  subnetwork = google_compute_subnetwork.olist.name

  # Remove default node pool — usamos node pool separado
  remove_default_node_pool = true
  initial_node_count       = 1

  release_channel {
    channel = var.kubernetes_version
  }

  ip_allocation_policy {
    cluster_secondary_range_name  = "pods"
    services_secondary_range_name = "services"
  }

  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  depends_on = [google_project_service.apis]
}

resource "google_container_node_pool" "olist" {
  name     = "${var.cluster_name}-nodes"
  cluster  = google_container_cluster.olist.id
  location = var.region

  autoscaling {
    min_node_count = 1
    max_node_count = var.node_max_count
  }

  initial_node_count = var.node_count

  node_config {
    machine_type = var.node_machine_type
    disk_size_gb = 50
    disk_type    = "pd-ssd"

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform",
    ]

    labels = var.labels

    workload_metadata_config {
      mode = "GKE_METADATA"
    }
  }
}

# =============================================================================
# Cloud SQL — PostgreSQL 14
# =============================================================================
resource "google_compute_global_address" "sql_private_ip" {
  name          = "${var.cluster_name}-sql-ip"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.olist.id
}

resource "google_service_networking_connection" "sql_vpc" {
  network                 = google_compute_network.olist.id
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.sql_private_ip.name]
}

resource "google_sql_database_instance" "olist" {
  name             = "${var.cluster_name}-postgres"
  database_version = "POSTGRES_14"
  region           = var.region

  settings {
    tier = var.db_tier

    ip_configuration {
      ipv4_enabled    = false
      private_network = google_compute_network.olist.id
    }

    backup_configuration {
      enabled    = true
      start_time = "03:00"
    }
  }

  deletion_protection = true
  depends_on          = [google_service_networking_connection.sql_vpc]
}

resource "google_sql_database" "olist" {
  name     = "olist"
  instance = google_sql_database_instance.olist.name
}

resource "google_sql_user" "olist" {
  name     = "olist"
  instance = google_sql_database_instance.olist.name
  password = var.db_password
}

# =============================================================================
# Static IP para Ingress
# =============================================================================
resource "google_compute_global_address" "olist_ingress" {
  name = "olist-static-ip"
}
