# =============================================================================
# Resource Group
# =============================================================================
resource "azurerm_resource_group" "olist" {
  name     = var.resource_group_name
  location = var.location
  tags     = var.tags
}

# =============================================================================
# Virtual Network
# =============================================================================
resource "azurerm_virtual_network" "olist" {
  name                = "${var.cluster_name}-vnet"
  location            = azurerm_resource_group.olist.location
  resource_group_name = azurerm_resource_group.olist.name
  address_space       = ["10.0.0.0/8"]
  tags                = var.tags
}

resource "azurerm_subnet" "aks" {
  name                 = "aks-subnet"
  resource_group_name  = azurerm_resource_group.olist.name
  virtual_network_name = azurerm_virtual_network.olist.name
  address_prefixes     = ["10.1.0.0/16"]
}

# =============================================================================
# ACR — Azure Container Registry
# =============================================================================
resource "azurerm_container_registry" "olist" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.olist.name
  location            = azurerm_resource_group.olist.location
  sku                 = "Standard"
  admin_enabled       = false
  tags                = var.tags
}

# =============================================================================
# AKS Cluster
# =============================================================================
resource "azurerm_kubernetes_cluster" "olist" {
  name                = var.cluster_name
  location            = azurerm_resource_group.olist.location
  resource_group_name = azurerm_resource_group.olist.name
  dns_prefix          = var.cluster_name
  kubernetes_version  = var.kubernetes_version
  tags                = var.tags

  default_node_pool {
    name                = "default"
    vm_size             = var.node_vm_size
    node_count          = var.node_count
    min_count           = 1
    max_count           = var.node_max_count
    enable_auto_scaling = true
    vnet_subnet_id      = azurerm_subnet.aks.id
    os_disk_size_gb     = 50
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin    = "azure"
    load_balancer_sku = "standard"
  }

  ingress_application_gateway {
    gateway_name = "${var.cluster_name}-agw"
    subnet_cidr  = "10.2.0.0/16"
  }
}

# Permite AKS fazer pull do ACR
resource "azurerm_role_assignment" "aks_acr_pull" {
  principal_id                     = azurerm_kubernetes_cluster.olist.kubelet_identity[0].object_id
  role_definition_name             = "AcrPull"
  scope                            = azurerm_container_registry.olist.id
  skip_service_principal_aad_check = true
}

# =============================================================================
# Azure Database for PostgreSQL Flexible Server
# =============================================================================
resource "azurerm_postgresql_flexible_server" "olist" {
  name                   = "${var.cluster_name}-postgres"
  resource_group_name    = azurerm_resource_group.olist.name
  location               = azurerm_resource_group.olist.location
  version                = "14"
  administrator_login    = "olist"
  administrator_password = var.db_password
  sku_name               = var.db_sku
  storage_mb             = var.db_storage_mb
  backup_retention_days  = 7
  tags                   = var.tags
}

resource "azurerm_postgresql_flexible_server_database" "olist" {
  name      = "olist"
  server_id = azurerm_postgresql_flexible_server.olist.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "aks" {
  name             = "allow-aks"
  server_id        = azurerm_postgresql_flexible_server.olist.id
  start_ip_address = "10.0.0.0"
  end_ip_address   = "10.255.255.255"
}
