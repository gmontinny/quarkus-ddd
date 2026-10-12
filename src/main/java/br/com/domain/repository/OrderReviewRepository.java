package br.com.domain.repository;

import br.com.domain.model.OrderReview;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderReviewRepository {
    Uni<OrderReview> findById(String reviewId);
    Uni<List<OrderReview>> findByOrderId(String orderId);
    Uni<OrderReview> save(OrderReview review);
    Uni<OrderReview> update(OrderReview review);
    Uni<Boolean> deleteById(String reviewId);
}
