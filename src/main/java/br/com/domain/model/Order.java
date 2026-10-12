package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntityBase {

    @Id
    @Column(name = "order_id")
    public String orderId;

    @Column(name = "customer_id", nullable = false)
    public String customerId;

    @Column(name = "order_status")
    public String orderStatus;

    @Column(name = "order_purchase_timestamp")
    public LocalDateTime orderPurchaseTimestamp;

    @Column(name = "order_approved_at")
    public LocalDateTime orderApprovedAt;

    @Column(name = "order_delivered_carrier_date")
    public LocalDateTime orderDeliveredCarrierDate;

    @Column(name = "order_delivered_customer_date")
    public LocalDateTime orderDeliveredCustomerDate;

    @Column(name = "order_estimated_delivery_date")
    public LocalDateTime orderEstimatedDeliveryDate;
}
