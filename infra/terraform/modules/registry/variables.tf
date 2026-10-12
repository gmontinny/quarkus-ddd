variable "registry_name" {
  description = "Container registry name"
  type        = string
  default     = "olist"
}

variable "region" {
  description = "Cloud region"
  type        = string
}

variable "tags" {
  type    = map(string)
  default = {}
}
