package br.com.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderItemResponse(
        String orderId,
        Integer orderItemId,
        String productId,
        String sellerId,
        LocalDateTime shippingLimitDate,
        BigDecimal price,
        BigDecimal freightValue,
        List<Link> links
) {}
