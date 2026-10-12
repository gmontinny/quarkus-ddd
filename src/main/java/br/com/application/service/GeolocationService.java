package br.com.application.service;

import br.com.application.dto.response.GeolocationResponse;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.PageResponse;
import br.com.application.mapper.GeolocationMapper;
import br.com.domain.repository.GeolocationRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class GeolocationService {

    @Inject GeolocationRepository repository;
    @Inject GeolocationMapper mapper;

    @WithSession
    public Uni<PageResponse<GeolocationResponse>> findByZipCode(String zipCode, int page, int size) {
        return Uni.combine().all()
                .unis(repository.findByZipCode(zipCode, page, size), repository.countByZipCode(zipCode))
                .asTuple()
                .map(t -> {
                    var responses = t.getItem1().stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, t.getItem2(), buildLinks("zip/" + zipCode, page, size));
                });
    }

    @WithSession
    public Uni<PageResponse<GeolocationResponse>> findByState(String state, int page, int size) {
        return Uni.combine().all()
                .unis(repository.findByState(state, page, size), repository.countByState(state))
                .asTuple()
                .map(t -> {
                    var responses = t.getItem1().stream().map(mapper::toResponse).toList();
                    return PageResponse.of(responses, page, size, t.getItem2(), buildLinks("state/" + state, page, size));
                });
    }

    private List<Link> buildLinks(String path, int page, int size) {
        return List.of(
                Link.self("/api/v1/geolocation/" + path + "?page=" + page + "&size=" + size),
                Link.of("next", "/api/v1/geolocation/" + path + "?page=" + (page + 1) + "&size=" + size, "GET")
        );
    }
}
