variable "location" {
  description = "Azure region"
  type        = string
  default     = "eastus"
}

variable "resource_group_name" {
  description = "Azure resource group name"
  type        = string
  default     = "olist-rg"
}

variable "cluster_name" {
  description = "AKS cluster name"
  type        = string
  default     = "olist-aks"
}

variable "kubernetes_version" {
  description = "Kubernetes version"
  type        = string
  default     = "1.29"
}

variable "node_vm_size" {
  description = "VM size for AKS nodes"
  type        = string
  default     = "Standard_D2s_v3"
}

variable "node_count" {
  description = "Number of AKS nodes"
  type        = number
  default     = 2
}

variable "node_max_count" {
  description = "Maximum AKS nodes for autoscaling"
  type        = number
  default     = 5
}

variable "acr_name" {
  description = "Azure Container Registry name (globally unique)"
  type        = string
  default     = "olistacr"
}

variable "db_password" {
  description = "PostgreSQL admin password"
  type        = string
  sensitive   = true
}

variable "db_sku" {
  description = "Azure Database for PostgreSQL SKU"
  type        = string
  default     = "B_Standard_B1ms"
}

variable "db_storage_mb" {
  description = "Database storage in MB"
  type        = number
  default     = 32768
}

variable "tags" {
  type = map(string)
  default = {
    Project     = "olist"
    Environment = "production"
    ManagedBy   = "terraform"
  }
}
