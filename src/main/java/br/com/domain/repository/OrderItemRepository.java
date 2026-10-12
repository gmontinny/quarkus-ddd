package br.com.domain.repository;

import br.com.domain.model.OrderItem;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderItemRepository {
    Uni<List<OrderItem>> findByOrderId(String orderId);
    Uni<OrderItem> save(OrderItem item);
    Uni<Boolean> deleteByOrderId(String orderId);
}
