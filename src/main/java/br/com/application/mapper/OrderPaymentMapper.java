package br.com.application.mapper;

import br.com.application.dto.request.OrderPaymentRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.OrderPaymentResponse;
import br.com.domain.model.OrderPayment;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderPaymentMapper {

    public OrderPayment toDomain(OrderPaymentRequest req) {
        var payment = new OrderPayment();
        payment.orderId = req.orderId();
        payment.paymentSequential = req.paymentSequential();
        payment.paymentType = req.paymentType();
        payment.paymentInstallments = req.paymentInstallments();
        payment.paymentValue = req.paymentValue();
        return payment;
    }

    public OrderPaymentResponse toResponse(OrderPayment p) {
        return new OrderPaymentResponse(
                p.orderId, p.paymentSequential, p.paymentType,
                p.paymentInstallments, p.paymentValue,
                List.of(Link.of("order", "/api/v1/orders/" + p.orderId, "GET"))
        );
    }
}
