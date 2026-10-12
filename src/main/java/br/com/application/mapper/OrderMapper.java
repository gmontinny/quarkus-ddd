package br.com.application.mapper;

import br.com.application.dto.request.OrderRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.OrderResponse;
import br.com.domain.model.Order;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderMapper {

    public Order toDomain(OrderRequest req) {
        var o = new Order();
        o.orderId = req.orderId();
        o.customerId = req.customerId();
        o.orderStatus = req.orderStatus();
        o.orderPurchaseTimestamp = req.orderPurchaseTimestamp();
        o.orderApprovedAt = req.orderApprovedAt();
        o.orderDeliveredCarrierDate = req.orderDeliveredCarrierDate();
        o.orderDeliveredCustomerDate = req.orderDeliveredCustomerDate();
        o.orderEstimatedDeliveryDate = req.orderEstimatedDeliveryDate();
        return o;
    }

    public OrderResponse toResponse(Order o) {
        return new OrderResponse(
                o.orderId, o.customerId, o.orderStatus,
                o.orderPurchaseTimestamp, o.orderApprovedAt,
                o.orderDeliveredCarrierDate, o.orderDeliveredCustomerDate,
                o.orderEstimatedDeliveryDate,
                buildLinks(o.orderId, o.customerId)
        );
    }

    private List<Link> buildLinks(String orderId, String customerId) {
        return List.of(
                Link.self("/api/v1/orders/" + orderId),
                Link.of("customer", "/api/v1/customers/" + customerId, "GET"),
                Link.of("items", "/api/v1/orders/" + orderId + "/items", "GET"),
                Link.of("payments", "/api/v1/orders/" + orderId + "/payments", "GET"),
                Link.of("update", "/api/v1/orders/" + orderId, "PUT"),
                Link.of("delete", "/api/v1/orders/" + orderId, "DELETE")
        );
    }
}
