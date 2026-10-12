package br.com.interfaces.rest;

import br.com.application.dto.request.OrderItemRequest;
import br.com.application.dto.response.OrderItemResponse;
import br.com.application.service.OrderItemService;
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

@Path("/api/v1/orders/{orderId}/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Order Items", description = "Items of an order")
public class OrderItemResource {

    @Inject OrderItemService service;

    @GET
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List all items of an order")
    public Uni<List<OrderItemResponse>> findByOrderId(@PathParam("orderId") String orderId) {
        return service.findByOrderId(orderId);
    }

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Add an item to an order")
    public Uni<Response> create(@PathParam("orderId") String orderId, @Valid OrderItemRequest request) {
        return service.create(request)
                .map(item -> Response.status(Response.Status.CREATED).entity(item).build());
    }

    @DELETE
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete all items of an order")
    public Uni<Response> deleteByOrderId(@PathParam("orderId") String orderId) {
        return service.deleteByOrderId(orderId).map(v -> Response.noContent().build());
    }
}
