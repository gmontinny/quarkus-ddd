package br.com.application.service;

import br.com.application.dto.request.OrderPaymentRequest;
import br.com.application.dto.response.OrderPaymentResponse;
import br.com.application.mapper.OrderPaymentMapper;
import br.com.domain.repository.OrderPaymentRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class OrderPaymentService {

    @Inject OrderPaymentRepository repository;
    @Inject OrderPaymentMapper mapper;

    @WithSession
    public Uni<List<OrderPaymentResponse>> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .map(list -> list.stream().map(mapper::toResponse).toList());
    }

    @WithTransaction
    public Uni<OrderPaymentResponse> create(OrderPaymentRequest request) {
        return repository.save(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> deleteByOrderId(String orderId) {
        return repository.deleteByOrderId(orderId).replaceWithVoid();
    }
}
