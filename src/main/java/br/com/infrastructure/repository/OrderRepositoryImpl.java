package br.com.infrastructure.repository;

import br.com.domain.model.Order;
import br.com.domain.repository.OrderRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderRepositoryImpl implements OrderRepository, PanacheRepositoryBase<Order, String> {

    @Override
    public Uni<Order> findById(String orderId) {
        return find("orderId", orderId).firstResult();
    }

    @Override
    public Uni<List<Order>> findAll(int page, int size) {
        return findAll().page(page, size).list();
    }

    @Override
    public Uni<List<Order>> findByCustomerId(String customerId, int page, int size) {
        return find("customerId", customerId).page(page, size).list();
    }

    @Override
    public Uni<List<Order>> findByStatus(String status, int page, int size) {
        return find("orderStatus", status).page(page, size).list();
    }

    @Override
    public Uni<Order> save(Order order) {
        return persist(order);
    }

    @Override
    public Uni<Order> update(Order order) {
        return findById(order.orderId)
                .onItem().ifNotNull().transformToUni(existing -> {
                    existing.orderStatus = order.orderStatus;
                    existing.orderApprovedAt = order.orderApprovedAt;
                    existing.orderDeliveredCarrierDate = order.orderDeliveredCarrierDate;
                    existing.orderDeliveredCustomerDate = order.orderDeliveredCustomerDate;
                    existing.orderEstimatedDeliveryDate = order.orderEstimatedDeliveryDate;
                    return persist(existing);
                });
    }

    @Override
    public Uni<Boolean> deleteById(String orderId) {
        return delete("orderId", orderId).map(count -> count > 0);
    }

    @Override
    public Uni<Long> count() {
        return count("from Order");
    }
}
