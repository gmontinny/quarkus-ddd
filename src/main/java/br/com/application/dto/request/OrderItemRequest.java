package br.com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemRequest(
        @NotBlank String orderId,
        @NotNull Integer orderItemId,
        String productId,
        String sellerId,
        LocalDateTime shippingLimitDate,
        BigDecimal price,
        BigDecimal freightValue
) {}
