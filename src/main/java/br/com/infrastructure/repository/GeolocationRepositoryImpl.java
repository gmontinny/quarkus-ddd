package br.com.infrastructure.repository;

import br.com.domain.model.Geolocation;
import br.com.domain.repository.GeolocationRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class GeolocationRepositoryImpl implements GeolocationRepository, PanacheRepositoryBase<Geolocation, Long> {

    @Override
    public Uni<List<Geolocation>> findByZipCode(String zipCode, int page, int size) {
        return find("zipCodePrefix", zipCode).page(page, size).list();
    }

    @Override
    public Uni<List<Geolocation>> findByState(String state, int page, int size) {
        return find("state", state).page(page, size).list();
    }

    @Override
    public Uni<Long> countByZipCode(String zipCode) {
        return count("zipCodePrefix", zipCode);
    }

    @Override
    public Uni<Long> countByState(String state) {
        return count("state", state);
    }
}
