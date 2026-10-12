package br.com.application.service;

import br.com.application.dto.request.ProductRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.PageResponse;
import br.com.application.dto.response.ProductResponse;
import br.com.application.mapper.ProductMapper;
import br.com.domain.exception.ResourceNotFoundException;
import br.com.domain.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject ProductRepository repository;
    @Inject ProductMapper mapper;

    @WithSession
    public Uni<PageResponse<ProductResponse>> findAll(int page, int size) {
        return Uni.combine().all()
                .unis(repository.findAll(page, size), repository.count())
                .asTuple()
                .map(t -> {
                    var responses = t.getItem1().stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, t.getItem2(), buildPageLinks(page, size));
                });
    }

    @WithSession
    public Uni<ProductResponse> findById(String id) {
        return repository.findById(id)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Product not found: " + id))
                .map(mapper::toResponse);
    }

    @WithSession
    public Uni<PageResponse<ProductResponse>> findByCategory(String category, int page, int size) {
        return repository.findByCategory(category, page, size)
                .map(list -> {
                    var responses = list.stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, (long) responses.size(), buildPageLinks(page, size));
                });
    }

    @WithTransaction
    public Uni<ProductResponse> create(ProductRequest request) {
        return repository.save(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<ProductResponse> update(String id, ProductRequest request) {
        var product = mapper.toDomain(request);
        product.productId = id;
        return repository.update(product)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Product not found: " + id))
                .map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> delete(String id) {
        return repository.deleteById(id)
                .map(deleted -> {
                    if (!deleted) throw new ResourceNotFoundException("Product not found: " + id);
                    return null;
                });
    }

    private List<Link> buildPageLinks(int page, int size) {
        return List.of(
                Link.self("/api/v1/products?page=" + page + "&size=" + size),
                Link.of("next", "/api/v1/products?page=" + (page + 1) + "&size=" + size, "GET")
        );
    }
}
