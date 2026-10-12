package br.com.interfaces.rest;

import br.com.application.dto.request.ProductRequest;
import br.com.application.dto.response.PageResponse;
import br.com.application.dto.response.ProductResponse;
import br.com.application.service.ProductService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Product CRUD operations")
public class ProductResource {

    @Inject ProductService service;

    @GET
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List all products (paginated)")
    public Uni<PageResponse<ProductResponse>> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Get product by ID")
    public Uni<ProductResponse> findById(@PathParam("id") String id) {
        return service.findById(id);
    }

    @GET
    @Path("/category/{category}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List products by category")
    public Uni<PageResponse<ProductResponse>> findByCategory(
            @PathParam("category") String category,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.findByCategory(category, page, size);
    }

    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Create a new product")
    public Uni<Response> create(@Valid ProductRequest request) {
        return service.create(request)
                .map(p -> Response.created(java.net.URI.create("/api/v1/products/" + p.productId())).entity(p).build());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Update product")
    public Uni<ProductResponse> update(@PathParam("id") String id, @Valid ProductRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete product")
    public Uni<Response> delete(@PathParam("id") String id) {
        return service.delete(id).map(v -> Response.noContent().build());
    }
}
