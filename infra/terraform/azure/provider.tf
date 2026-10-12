terraform {
  required_version = ">= 1.6"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.90"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.25"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.12"
    }
  }

  backend "azurerm" {
    resource_group_name  = "olist-terraform-rg"
    storage_account_name = "olistterraformstate"
    container_name       = "tfstate"
    key                  = "azure/terraform.tfstate"
  }
}

provider "azurerm" {
  features {}
}

provider "kubernetes" {
  host                   = azurerm_kubernetes_cluster.olist.kube_config[0].host
  client_certificate     = base64decode(azurerm_kubernetes_cluster.olist.kube_config[0].client_certificate)
  client_key             = base64decode(azurerm_kubernetes_cluster.olist.kube_config[0].client_key)
  cluster_ca_certificate = base64decode(azurerm_kubernetes_cluster.olist.kube_config[0].cluster_ca_certificate)
}

provider "helm" {
  kubernetes {
    host                   = azurerm_kubernetes_cluster.olist.kube_config[0].host
    client_certificate     = base64decode(azurerm_kubernetes_cluster.olist.kube_config[0].client_certificate)
    client_key             = base64decode(azurerm_kubernetes_cluster.olist.kube_config[0].client_key)
    cluster_ca_certificate = base64decode(azurerm_kubernetes_cluster.olist.kube_config[0].cluster_ca_certificate)
  }
}
