package br.com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerRequest(
        @NotBlank String sellerId,
        String sellerZipCodePrefix,
        String sellerCity,
        @Size(min = 2, max = 2) String sellerState
) {}
