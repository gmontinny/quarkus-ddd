package br.com.application.service;

import br.com.application.dto.request.OrderRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.OrderResponse;
import br.com.application.mapper.OrderMapper;
import br.com.domain.exception.ResourceNotFoundException;
import br.com.domain.model.Order;
import br.com.domain.repository.OrderRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class OrderServiceTest {

    @Inject OrderService orderService;
    @InjectMock OrderRepository orderRepository;
    @InjectMock OrderMapper orderMapper;

    private Order sampleOrder() {
        var o = new Order();
        o.orderId = "order-001";
        o.customerId = "cust-001";
        o.orderStatus = "delivered";
        return o;
    }

    private OrderResponse sampleResponse() {
        return new OrderResponse(
                "order-001", "cust-001", "delivered",
                null, null, null, null, null,
                List.of(Link.self("/api/v1/orders/order-001"))
        );
    }

    @Test
    @RunOnVertxContext
    void testFindById_found(UniAsserter asserter) {
        Mockito.when(orderRepository.findById("order-001"))
                .thenReturn(Uni.createFrom().item(sampleOrder()));
        Mockito.when(orderMapper.toResponse(Mockito.any()))
                .thenReturn(sampleResponse());

        asserter.assertThat(
                () -> orderService.findById("order-001"),
                result -> {
                    assertNotNull(result);
                    assertEquals("order-001", result.orderId());
                    assertEquals("delivered", result.orderStatus());
                });
    }

    @Test
    @RunOnVertxContext
    void testFindById_notFound(UniAsserter asserter) {
        Mockito.when(orderRepository.findById("missing"))
                .thenReturn(Uni.createFrom().nullItem());

        asserter.assertFailedWith(
                () -> orderService.findById("missing"),
                ResourceNotFoundException.class);
    }

    @Test
    @RunOnVertxContext
    void testCreate(UniAsserter asserter) {
        var request = new OrderRequest("order-002", "cust-001", "pending",
                null, null, null, null, null);
        var order = sampleOrder();
        var response = sampleResponse();

        Mockito.when(orderMapper.toDomain(request)).thenReturn(order);
        Mockito.when(orderRepository.save(order)).thenReturn(Uni.createFrom().item(order));
        Mockito.when(orderMapper.toResponse(order)).thenReturn(response);

        asserter.assertNotNull(() -> orderService.create(request));
    }
}
