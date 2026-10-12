package br.com.application.mapper;

import br.com.application.dto.response.GeolocationResponse;
import br.com.application.dto.response.Link;
import br.com.domain.model.Geolocation;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class GeolocationMapper {

    public GeolocationResponse toResponse(Geolocation g) {
        return new GeolocationResponse(
                g.id, g.zipCodePrefix, g.lat, g.lng, g.city, g.state,
                List.of(
                        Link.self("/api/v1/geolocation/zip/" + g.zipCodePrefix),
                        Link.of("state", "/api/v1/geolocation/state/" + g.state, "GET")
                )
        );
    }
}
