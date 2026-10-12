output "gke_cluster_name" {
  description = "GKE cluster name"
  value       = google_container_cluster.olist.name
}

output "gke_cluster_endpoint" {
  description = "GKE cluster endpoint"
  value       = google_container_cluster.olist.endpoint
  sensitive   = true
}

output "artifact_registry_url" {
  description = "Artifact Registry URL for olist-app"
  value       = "${var.region}-docker.pkg.dev/${var.project_id}/olist/olist-app"
}

output "cloud_sql_private_ip" {
  description = "Cloud SQL private IP"
  value       = google_sql_database_instance.olist.private_ip_address
  sensitive   = true
}

output "ingress_static_ip" {
  description = "Static IP for GKE Ingress"
  value       = google_compute_global_address.olist_ingress.address
}

output "configure_kubectl" {
  description = "Command to configure kubectl"
  value       = "gcloud container clusters get-credentials ${var.cluster_name} --region ${var.region} --project ${var.project_id}"
}
