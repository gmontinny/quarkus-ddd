package br.com.application.service;

import br.com.application.dto.request.CustomerRequest;
import br.com.application.dto.response.CustomerResponse;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.PageResponse;
import br.com.application.mapper.CustomerMapper;
import br.com.domain.exception.ResourceNotFoundException;
import br.com.domain.repository.CustomerRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CustomerService {

    @Inject CustomerRepository repository;
    @Inject CustomerMapper mapper;

    @WithSession
    public Uni<PageResponse<CustomerResponse>> findAll(int page, int size) {
        return Uni.combine().all()
                .unis(repository.findAll(page, size), repository.count())
                .asTuple()
                .map(t -> {
                    var responses = t.getItem1().stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, t.getItem2(), buildPageLinks(page, size));
                });
    }

    @WithSession
    public Uni<CustomerResponse> findById(String id) {
        return repository.findById(id)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Customer not found: " + id))
                .map(mapper::toResponse);
    }

    @WithSession
    public Uni<PageResponse<CustomerResponse>> findByState(String state, int page, int size) {
        return repository.findByState(state, page, size)
                .map(list -> {
                    var responses = list.stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, (long) responses.size(), buildPageLinks(page, size));
                });
    }

    @WithTransaction
    public Uni<CustomerResponse> create(CustomerRequest request) {
        return repository.save(mapper.toDomain(request)).map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<CustomerResponse> update(String id, CustomerRequest request) {
        var customer = mapper.toDomain(request);
        customer.customerId = id;
        return repository.update(customer)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Customer not found: " + id))
                .map(mapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> delete(String id) {
        return repository.deleteById(id)
                .map(deleted -> {
                    if (!deleted) throw new ResourceNotFoundException("Customer not found: " + id);
                    return null;
                });
    }

    private List<Link> buildPageLinks(int page, int size) {
        return List.of(
                Link.self("/api/v1/customers?page=" + page + "&size=" + size),
                Link.of("next", "/api/v1/customers?page=" + (page + 1) + "&size=" + size, "GET"),
                page > 0 ? Link.of("prev", "/api/v1/customers?page=" + (page - 1) + "&size=" + size, "GET")
                         : Link.of("prev", "/api/v1/customers?page=0&size=" + size, "GET")
        );
    }
}
