package br.com.application.dto.response;

import java.util.List;

public record GeolocationResponse(
        Long id,
        String zipCodePrefix,
        Double lat,
        Double lng,
        String city,
        String state,
        List<Link> links
) {}
