output "cluster_name" {
  description = "Cluster name"
  value       = var.cluster_name
}

output "cluster_endpoint" {
  description = "Kubernetes API endpoint"
  value       = ""  # implementado por cada provider
}

output "kubeconfig" {
  description = "Kubeconfig content"
  value       = ""
  sensitive   = true
}
