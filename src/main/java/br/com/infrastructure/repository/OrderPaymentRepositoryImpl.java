package br.com.infrastructure.repository;

import br.com.domain.model.OrderPayment;
import br.com.domain.model.OrderPaymentId;
import br.com.domain.repository.OrderPaymentRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderPaymentRepositoryImpl implements OrderPaymentRepository, PanacheRepositoryBase<OrderPayment, OrderPaymentId> {

    @Override
    public Uni<List<OrderPayment>> findByOrderId(String orderId) {
        return find("orderId", orderId).list();
    }

    @Override
    public Uni<OrderPayment> save(OrderPayment payment) {
        return persist(payment);
    }

    @Override
    public Uni<Boolean> deleteByOrderId(String orderId) {
        return delete("orderId", orderId).map(count -> count > 0);
    }
}
