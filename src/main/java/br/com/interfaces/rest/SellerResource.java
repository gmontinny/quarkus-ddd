package br.com.interfaces.rest;

import br.com.application.dto.request.SellerRequest;
import br.com.application.dto.response.PageResponse;
import br.com.application.dto.response.SellerResponse;
import br.com.application.service.SellerService;
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

@Path("/api/v1/sellers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sellers", description = "Seller CRUD operations")
@SecurityRequirement(name = "BearerAuth")
public class SellerResource {

    @Inject SellerService service;

    @GET
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List all sellers (paginated)")
    public Uni<PageResponse<SellerResponse>> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Get seller by ID")
    public Uni<SellerResponse> findById(@PathParam("id") String id) {
        return service.findById(id);
    }

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Create a new seller")
    public Uni<Response> create(@Valid SellerRequest request) {
        return service.create(request)
                .map(s -> Response.created(java.net.URI.create("/api/v1/sellers/" + s.sellerId())).entity(s).build());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Update seller")
    public Uni<SellerResponse> update(@PathParam("id") String id, @Valid SellerRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete seller")
    public Uni<Response> delete(@PathParam("id") String id) {
        return service.delete(id).map(v -> Response.noContent().build());
    }
}
