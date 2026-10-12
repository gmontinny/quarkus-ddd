package br.com.domain.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemId implements Serializable {

    public String orderId;
    public Integer orderItemId;

    public OrderItemId() {}

    public OrderItemId(String orderId, Integer orderItemId) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItemId that)) return false;
        return Objects.equals(orderId, that.orderId) && Objects.equals(orderItemId, that.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderItemId);
    }
}
