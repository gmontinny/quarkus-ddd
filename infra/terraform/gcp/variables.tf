variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "GCP zone"
  type        = string
  default     = "us-central1-a"
}

variable "cluster_name" {
  description = "GKE cluster name"
  type        = string
  default     = "olist-gke"
}

variable "kubernetes_version" {
  description = "GKE release channel"
  type        = string
  default     = "STABLE"
}

variable "node_machine_type" {
  description = "GCE machine type for GKE nodes"
  type        = string
  default     = "e2-standard-2"
}

variable "node_count" {
  description = "Number of GKE nodes per zone"
  type        = number
  default     = 1
}

variable "node_max_count" {
  description = "Maximum GKE nodes per zone"
  type        = number
  default     = 3
}

variable "db_tier" {
  description = "Cloud SQL instance tier"
  type        = string
  default     = "db-f1-micro"
}

variable "db_password" {
  description = "Cloud SQL postgres user password"
  type        = string
  sensitive   = true
}

variable "labels" {
  type = map(string)
  default = {
    project     = "olist"
    environment = "production"
    managed_by  = "terraform"
  }
}
