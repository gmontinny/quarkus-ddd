package br.com.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductRequest(
        @NotBlank String productId,
        String productCategoryName,
        Integer productNameLenght,
        Integer productDescriptionLenght,
        Integer productPhotosQty,
        Integer productWeightG,
        Integer productLengthCm,
        Integer productHeightCm,
        Integer productWidthCm
) {}
