package br.com.application.mapper;

import br.com.application.dto.request.OrderReviewRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.OrderReviewResponse;
import br.com.domain.model.OrderReview;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderReviewMapper {

    public OrderReview toDomain(OrderReviewRequest req) {
        var review = new OrderReview();
        review.reviewId = req.reviewId();
        review.orderId = req.orderId();
        review.reviewScore = req.reviewScore();
        review.reviewCommentTitle = req.reviewCommentTitle();
        review.reviewCommentMessage = req.reviewCommentMessage();
        review.reviewCreationDate = req.reviewCreationDate();
        review.reviewAnswerTimestamp = req.reviewAnswerTimestamp();
        return review;
    }

    public OrderReviewResponse toResponse(OrderReview r) {
        return new OrderReviewResponse(
                r.reviewId, r.orderId, r.reviewScore,
                r.reviewCommentTitle, r.reviewCommentMessage,
                r.reviewCreationDate, r.reviewAnswerTimestamp,
                List.of(
                        Link.self("/api/v1/reviews/" + r.reviewId),
                        Link.of("order", "/api/v1/orders/" + r.orderId, "GET"),
                        Link.of("update", "/api/v1/reviews/" + r.reviewId, "PUT"),
                        Link.of("delete", "/api/v1/reviews/" + r.reviewId, "DELETE")
                )
        );
    }
}
