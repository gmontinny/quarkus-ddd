package br.com.application.dto.response;

import java.util.List;

public record ProductResponse(
        String productId,
        String productCategoryName,
        Integer productNameLenght,
        Integer productDescriptionLenght,
        Integer productPhotosQty,
        Integer productWeightG,
        Integer productLengthCm,
        Integer productHeightCm,
        Integer productWidthCm,
        List<Link> links
) {}
