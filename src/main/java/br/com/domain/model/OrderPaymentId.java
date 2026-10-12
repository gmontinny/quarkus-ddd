package br.com.domain.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderPaymentId implements Serializable {

    public String orderId;
    public Integer paymentSequential;

    public OrderPaymentId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderPaymentId that)) return false;
        return Objects.equals(orderId, that.orderId) && Objects.equals(paymentSequential, that.paymentSequential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, paymentSequential);
    }
}
