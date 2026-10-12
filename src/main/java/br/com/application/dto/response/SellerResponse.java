package br.com.application.dto.response;

import java.util.List;

public record SellerResponse(
        String sellerId,
        String sellerZipCodePrefix,
        String sellerCity,
        String sellerState,
        List<Link> links
) {}
