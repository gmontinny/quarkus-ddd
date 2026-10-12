package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_payments")
@IdClass(OrderPaymentId.class)
public class OrderPayment extends PanacheEntityBase {

    @Id
    @Column(name = "order_id")
    public String orderId;

    @Id
    @Column(name = "payment_sequential")
    public Integer paymentSequential;

    @Column(name = "payment_type")
    public String paymentType;

    @Column(name = "payment_installments")
    public Integer paymentInstallments;

    @Column(name = "payment_value", precision = 10, scale = 2)
    public BigDecimal paymentValue;
}
