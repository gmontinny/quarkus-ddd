package br.com.interfaces.rest;

import br.com.application.dto.request.OrderRequest;
import br.com.application.dto.response.OrderResponse;
import br.com.application.dto.response.PageResponse;
import br.com.application.service.OrderService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Orders", description = "Order CRUD operations")
@SecurityRequirement(name = "BearerAuth")
public class OrderResource {

    @Inject OrderService service;

    @GET
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List all orders (paginated)")
    public Uni<PageResponse<OrderResponse>> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("customerId") String customerId,
            @QueryParam("status") String status) {
        if (customerId != null) return service.findByCustomerId(customerId, page, size);
        if (status != null) return service.findByStatus(status, page, size);
        return service.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Get order by ID")
    public Uni<OrderResponse> findById(@PathParam("id") String id) {
        return service.findById(id);
    }

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Create a new order")
    public Uni<Response> create(@Valid OrderRequest request) {
        return service.create(request)
                .map(o -> Response.created(java.net.URI.create("/api/v1/orders/" + o.orderId())).entity(o).build());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Update order")
    public Uni<OrderResponse> update(@PathParam("id") String id, @Valid OrderRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete order")
    public Uni<Response> delete(@PathParam("id") String id) {
        return service.delete(id).map(v -> Response.noContent().build());
    }
}
