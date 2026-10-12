package br.com.application.service;

import br.com.application.dto.request.OrderItemRequest;
import br.com.application.dto.response.OrderItemResponse;
import br.com.application.mapper.OrderItemMapper;
import br.com.domain.repository.OrderItemRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class OrderItemService {

    @Inject OrderItemRepository repository;
    @Inject OrderItemMapper mapper;

    @WithSession
    public Uni<List<OrderItemResponse>> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .map(list -> list.stream().map(mapper::toResponse).toList());
    }

    @WithTransaction
    public Uni<OrderItemResponse> create(OrderItemRequest request) {
        return repository.save(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> deleteByOrderId(String orderId) {
        return repository.deleteByOrderId(orderId).replaceWithVoid();
    }
}
