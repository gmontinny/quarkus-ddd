package br.com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record OrderRequest(
        @NotBlank String orderId,
        @NotBlank String customerId,
        String orderStatus,
        LocalDateTime orderPurchaseTimestamp,
        LocalDateTime orderApprovedAt,
        LocalDateTime orderDeliveredCarrierDate,
        LocalDateTime orderDeliveredCustomerDate,
        LocalDateTime orderEstimatedDeliveryDate
) {}
