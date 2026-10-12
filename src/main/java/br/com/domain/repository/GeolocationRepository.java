package br.com.domain.repository;

import br.com.domain.model.Geolocation;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface GeolocationRepository {
    Uni<List<Geolocation>> findByZipCode(String zipCode, int page, int size);
    Uni<List<Geolocation>> findByState(String state, int page, int size);
    Uni<Long> countByZipCode(String zipCode);
    Uni<Long> countByState(String state);
}
