package br.com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderPaymentRequest(
        @NotBlank String orderId,
        @NotNull Integer paymentSequential,
        String paymentType,
        Integer paymentInstallments,
        BigDecimal paymentValue
) {}
