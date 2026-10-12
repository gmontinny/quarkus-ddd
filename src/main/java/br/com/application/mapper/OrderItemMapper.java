package br.com.application.mapper;

import br.com.application.dto.request.OrderItemRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.OrderItemResponse;
import br.com.domain.model.OrderItem;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderItemMapper {

    public OrderItem toDomain(OrderItemRequest req) {
        var item = new OrderItem();
        item.orderId = req.orderId();
        item.orderItemId = req.orderItemId();
        item.productId = req.productId();
        item.sellerId = req.sellerId();
        item.shippingLimitDate = req.shippingLimitDate();
        item.price = req.price();
        item.freightValue = req.freightValue();
        return item;
    }

    public OrderItemResponse toResponse(OrderItem item) {
        return new OrderItemResponse(
                item.orderId, item.orderItemId, item.productId, item.sellerId,
                item.shippingLimitDate, item.price, item.freightValue,
                List.of(
                        Link.of("order", "/api/v1/orders/" + item.orderId, "GET"),
                        Link.of("product", "/api/v1/products/" + item.productId, "GET"),
                        Link.of("seller", "/api/v1/sellers/" + item.sellerId, "GET")
                )
        );
    }
}
