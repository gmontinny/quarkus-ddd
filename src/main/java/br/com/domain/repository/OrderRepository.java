package br.com.domain.repository;

import br.com.domain.model.Order;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderRepository {
    Uni<Order> findById(String orderId);
    Uni<List<Order>> findAll(int page, int size);
    Uni<List<Order>> findByCustomerId(String customerId, int page, int size);
    Uni<List<Order>> findByStatus(String status, int page, int size);
    Uni<Order> save(Order order);
    Uni<Order> update(Order order);
    Uni<Boolean> deleteById(String orderId);
    Uni<Long> count();
}
