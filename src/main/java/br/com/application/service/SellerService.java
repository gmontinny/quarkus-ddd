package br.com.application.service;

import br.com.application.dto.request.SellerRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.PageResponse;
import br.com.application.dto.response.SellerResponse;
import br.com.application.mapper.SellerMapper;
import br.com.domain.exception.ResourceNotFoundException;
import br.com.domain.repository.SellerRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class SellerService {

    @Inject SellerRepository repository;
    @Inject SellerMapper mapper;

    @WithSession
    public Uni<PageResponse<SellerResponse>> findAll(int page, int size) {
        return Uni.combine().all()
                .unis(repository.findAll(page, size), repository.count())
                .asTuple()
                .map(t -> {
                    var responses = t.getItem1().stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, t.getItem2(), buildPageLinks(page, size));
                });
    }

    @WithSession
    public Uni<SellerResponse> findById(String id) {
        return repository.findById(id)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Seller not found: " + id))
                .map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<SellerResponse> create(SellerRequest request) {
        return repository.save(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<SellerResponse> update(String id, SellerRequest request) {
        var seller = mapper.toDomain(request);
        seller.sellerId = id;
        return repository.update(seller)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Seller not found: " + id))
                .map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> delete(String id) {
        return repository.deleteById(id)
                .map(deleted -> {
                    if (!deleted) throw new ResourceNotFoundException("Seller not found: " + id);
                    return null;
                });
    }

    private List<Link> buildPageLinks(int page, int size) {
        return List.of(
                Link.self("/api/v1/sellers?page=" + page + "&size=" + size),
                Link.of("next", "/api/v1/sellers?page=" + (page + 1) + "&size=" + size, "GET")
        );
    }
}
