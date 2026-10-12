package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemId.class)
public class OrderItem extends PanacheEntityBase {

    @Id
    @Column(name = "order_id")
    public String orderId;

    @Id
    @Column(name = "order_item_id")
    public Integer orderItemId;

    @Column(name = "product_id")
    public String productId;

    @Column(name = "seller_id")
    public String sellerId;

    @Column(name = "shipping_limit_date")
    public LocalDateTime shippingLimitDate;

    @Column(name = "price", precision = 10, scale = 2)
    public BigDecimal price;

    @Column(name = "freight_value", precision = 10, scale = 2)
    public BigDecimal freightValue;
}
