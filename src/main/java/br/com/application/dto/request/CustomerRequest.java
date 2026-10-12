package br.com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank String customerId,
        @NotBlank String customerUniqueId,
        String customerZipCodePrefix,
        String customerCity,
        @Size(min = 2, max = 2) String customerState
) {}
