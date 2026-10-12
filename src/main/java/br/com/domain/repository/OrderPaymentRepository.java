package br.com.domain.repository;

import br.com.domain.model.OrderPayment;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderPaymentRepository {
    Uni<List<OrderPayment>> findByOrderId(String orderId);
    Uni<OrderPayment> save(OrderPayment payment);
    Uni<Boolean> deleteByOrderId(String orderId);
}
