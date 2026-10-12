package br.com.application.dto.response;

import java.util.List;

public record CustomerResponse(
        String customerId,
        String customerUniqueId,
        String customerZipCodePrefix,
        String customerCity,
        String customerState,
        List<Link> links
) {}
