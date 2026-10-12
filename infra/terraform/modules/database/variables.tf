variable "db_name" {
  description = "Database name"
  type        = string
  default     = "olist"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "olist"
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "Database instance class/tier"
  type        = string
}

variable "db_storage_gb" {
  description = "Database storage in GB"
  type        = number
  default     = 20
}

variable "region" {
  description = "Cloud region"
  type        = string
}

variable "subnet_ids" {
  description = "Subnet IDs for the database"
  type        = list(string)
  default     = []
}

variable "vpc_id" {
  description = "VPC/VNet ID"
  type        = string
  default     = ""
}

variable "tags" {
  type    = map(string)
  default = {}
}
