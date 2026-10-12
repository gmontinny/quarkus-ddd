package br.com.application.service;

import br.com.application.dto.request.OrderRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.OrderResponse;
import br.com.application.dto.response.PageResponse;
import br.com.application.mapper.OrderMapper;
import br.com.domain.exception.ResourceNotFoundException;
import br.com.domain.repository.OrderRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject OrderRepository repository;
    @Inject OrderMapper mapper;

    @WithSession
    public Uni<PageResponse<OrderResponse>> findAll(int page, int size) {
        return Uni.combine().all()
                .unis(repository.findAll(page, size), repository.count())
                .asTuple()
                .map(t -> {
                    var responses = t.getItem1().stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, t.getItem2(), buildPageLinks(page, size));
                });
    }

    @WithSession
    public Uni<OrderResponse> findById(String id) {
        return repository.findById(id)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Order not found: " + id))
                .map(mapper::toResponse);
    }

    @WithSession
    public Uni<PageResponse<OrderResponse>> findByCustomerId(String customerId, int page, int size) {
        return repository.findByCustomerId(customerId, page, size)
                .map(list -> {
                    var responses = list.stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, (long) responses.size(), buildPageLinks(page, size));
                });
    }

    @WithSession
    public Uni<PageResponse<OrderResponse>> findByStatus(String status, int page, int size) {
        return repository.findByStatus(status, page, size)
                .map(list -> {
                    var responses = list.stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, (long) responses.size(), buildPageLinks(page, size));
                });
    }

    @WithTransaction
    public Uni<OrderResponse> create(OrderRequest request) {
        return repository.save(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<OrderResponse> update(String id, OrderRequest request) {
        var order = mapper.toDomain(request);
        order.orderId = id;
        return repository.update(order)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Order not found: " + id))
                .map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> delete(String id) {
        return repository.deleteById(id)
                .map(deleted -> {
                    if (!deleted) throw new ResourceNotFoundException("Order not found: " + id);
                    return null;
                });
    }

    private List<Link> buildPageLinks(int page, int size) {
        return List.of(
                Link.self("/api/v1/orders?page=" + page + "&size=" + size),
                Link.of("next", "/api/v1/orders?page=" + (page + 1) + "&size=" + size, "GET")
        );
    }
}
