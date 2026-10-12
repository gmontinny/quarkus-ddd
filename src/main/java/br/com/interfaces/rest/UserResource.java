package br.com.interfaces.rest;

import br.com.application.dto.request.UserRequest;
import br.com.application.dto.response.UserResponse;
import br.com.infrastructure.security.KeycloakUserService;
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

import java.net.URI;
import java.util.List;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management via Keycloak")
@SecurityRequirement(name = "BearerAuth")
public class UserResource {

    @Inject
    KeycloakUserService userService;

    @GET
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "List all users (paginated) — ROLE_ADMIN only")
    public Uni<List<UserResponse>> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return userService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Get user by Keycloak ID — ROLE_ADMIN only")
    public Uni<UserResponse> findById(@PathParam("id") String id) {
        return userService.findById(id);
    }

    @POST
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Create a new user in Keycloak — ROLE_ADMIN only")
    public Uni<Response> create(@Valid UserRequest request) {
        return userService.create(request)
                .map(user -> Response.created(URI.create("/api/v1/users/" + user.id()))
                        .entity(user).build());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Update user in Keycloak — ROLE_ADMIN only")
    public Uni<UserResponse> update(@PathParam("id") String id, @Valid UserRequest request) {
        return userService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete user from Keycloak — ROLE_ADMIN only")
    public Uni<Response> delete(@PathParam("id") String id) {
        return userService.delete(id)
                .map(v -> Response.noContent().build());
    }
}
