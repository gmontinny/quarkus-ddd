package br.com.infrastructure.repository;

import br.com.domain.model.OrderItem;
import br.com.domain.model.OrderItemId;
import br.com.domain.repository.OrderItemRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderItemRepositoryImpl implements OrderItemRepository, PanacheRepositoryBase<OrderItem, OrderItemId> {

    @Override
    public Uni<List<OrderItem>> findByOrderId(String orderId) {
        return find("orderId", orderId).list();
    }

    @Override
    public Uni<OrderItem> save(OrderItem item) {
        return persist(item);
    }

    @Override
    public Uni<Boolean> deleteByOrderId(String orderId) {
        return delete("orderId", orderId).map(count -> count > 0);
    }
}
