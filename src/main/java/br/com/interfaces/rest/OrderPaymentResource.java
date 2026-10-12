package br.com.interfaces.rest;

import br.com.application.dto.request.OrderPaymentRequest;
import br.com.application.dto.response.OrderPaymentResponse;
import br.com.application.service.OrderPaymentService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

@Path("/api/v1/orders/{orderId}/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Order Payments", description = "Payments of an order")
public class OrderPaymentResource {

    @Inject OrderPaymentService service;

    @GET
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List all payments of an order")
    public Uni<List<OrderPaymentResponse>> findByOrderId(@PathParam("orderId") String orderId) {
        return service.findByOrderId(orderId);
    }

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Add a payment to an order")
    public Uni<Response> create(@PathParam("orderId") String orderId, @Valid OrderPaymentRequest request) {
        return service.create(request)
                .map(payment -> Response.status(Response.Status.CREATED).entity(payment).build());
    }

    @DELETE
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete all payments of an order")
    public Uni<Response> deleteByOrderId(@PathParam("orderId") String orderId) {
        return service.deleteByOrderId(orderId).map(v -> Response.noContent().build());
    }
}
