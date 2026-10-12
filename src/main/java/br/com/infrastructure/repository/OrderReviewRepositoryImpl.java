package br.com.infrastructure.repository;

import br.com.domain.model.OrderReview;
import br.com.domain.repository.OrderReviewRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderReviewRepositoryImpl implements OrderReviewRepository, PanacheRepositoryBase<OrderReview, String> {

    @Override
    public Uni<OrderReview> findById(String reviewId) {
        return find("reviewId", reviewId).firstResult();
    }

    @Override
    public Uni<List<OrderReview>> findByOrderId(String orderId) {
        return find("orderId", orderId).list();
    }

    @Override
    public Uni<OrderReview> save(OrderReview review) {
        return persist(review);
    }

    @Override
    public Uni<OrderReview> update(OrderReview review) {
        return findById(review.reviewId)
                .onItem().ifNotNull().transformToUni(existing -> {
                    existing.reviewScore = review.reviewScore;
                    existing.reviewCommentTitle = review.reviewCommentTitle;
                    existing.reviewCommentMessage = review.reviewCommentMessage;
                    existing.reviewAnswerTimestamp = review.reviewAnswerTimestamp;
                    return persist(existing);
                });
    }

    @Override
    public Uni<Boolean> deleteById(String reviewId) {
        return delete("reviewId", reviewId).map(count -> count > 0);
    }
}
