package br.com.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderId,
        String customerId,
        String orderStatus,
        LocalDateTime orderPurchaseTimestamp,
        LocalDateTime orderApprovedAt,
        LocalDateTime orderDeliveredCarrierDate,
        LocalDateTime orderDeliveredCustomerDate,
        LocalDateTime orderEstimatedDeliveryDate,
        List<Link> links
) {}
