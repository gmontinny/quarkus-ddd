package br.com.application.service;

import br.com.application.dto.request.OrderReviewRequest;
import br.com.application.dto.response.OrderReviewResponse;
import br.com.application.mapper.OrderReviewMapper;
import br.com.domain.exception.ResourceNotFoundException;
import br.com.domain.repository.OrderReviewRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class OrderReviewService {

    @Inject OrderReviewRepository repository;
    @Inject OrderReviewMapper mapper;

    @WithSession
    public Uni<OrderReviewResponse> findById(String reviewId) {
        return repository.findById(reviewId)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Review not found: " + reviewId))
                .map(mapper::toResponse);
    }

    @WithSession
    public Uni<List<OrderReviewResponse>> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .map(list -> list.stream().map(mapper::toResponse).toList());
    }

    @WithTransaction
    public Uni<OrderReviewResponse> create(OrderReviewRequest request) {
        return repository.save(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<OrderReviewResponse> update(String reviewId, OrderReviewRequest request) {
        var review = mapper.toDomain(request);
        review.reviewId = reviewId;
        return repository.update(review)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Review not found: " + reviewId))
                .map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> delete(String reviewId) {
        return repository.deleteById(reviewId)
                .map(deleted -> {
                    if (!deleted) throw new ResourceNotFoundException("Review not found: " + reviewId);
                    return null;
                });
    }
}
