package br.com.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaymentResponse(
        String orderId,
        Integer paymentSequential,
        String paymentType,
        Integer paymentInstallments,
        BigDecimal paymentValue,
        List<Link> links
) {}
