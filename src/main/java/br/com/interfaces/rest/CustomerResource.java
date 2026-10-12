package br.com.interfaces.rest;

import br.com.application.dto.request.CustomerRequest;
import br.com.application.dto.response.CustomerResponse;
import br.com.application.dto.response.PageResponse;
import br.com.application.service.CustomerService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customers", description = "Customer CRUD operations")
public class CustomerResource {

    @Inject CustomerService service;

    @GET
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List all customers (paginated)")
    public Uni<PageResponse<CustomerResponse>> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Get customer by ID")
    public Uni<CustomerResponse> findById(@PathParam("id") String id) {
        return service.findById(id);
    }

    @GET
    @Path("/state/{state}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List customers by state")
    public Uni<PageResponse<CustomerResponse>> findByState(
            @PathParam("state") String state,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.findByState(state, page, size);
    }

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Create a new customer")
    public Uni<Response> create(@Valid CustomerRequest request) {
        return service.create(request)
                .map(c -> Response.created(java.net.URI.create("/api/v1/customers/" + c.customerId())).entity(c).build());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Update customer")
    public Uni<CustomerResponse> update(@PathParam("id") String id, @Valid CustomerRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete customer")
    public Uni<Response> delete(@PathParam("id") String id) {
        return service.delete(id).map(v -> Response.noContent().build());
    }
}
