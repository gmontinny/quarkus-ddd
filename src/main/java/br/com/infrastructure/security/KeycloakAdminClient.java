package br.com.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@RegisterRestClient(configKey = "keycloak-admin-client")
@Path("/admin/realms/{realm}")
public interface KeycloakAdminClient {

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<List<KeycloakUserRepresentation>> listUsers(
            @PathParam("realm") String realm,
            @HeaderParam("Authorization") String authorization,
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("20") int max,
            @QueryParam("username") String username,
            @QueryParam("exact") Boolean exact
    );

    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<KeycloakUserRepresentation> getUser(
            @PathParam("realm") String realm,
            @PathParam("id") String id,
            @HeaderParam("Authorization") String authorization
    );

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Void> createUser(
            @PathParam("realm") String realm,
            @HeaderParam("Authorization") String authorization,
            KeycloakUserRepresentation user
    );

    @PUT
    @Path("/users/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Void> updateUser(
            @PathParam("realm") String realm,
            @PathParam("id") String id,
            @HeaderParam("Authorization") String authorization,
            KeycloakUserRepresentation user
    );

    @DELETE
    @Path("/users/{id}")
    Uni<Void> deleteUser(
            @PathParam("realm") String realm,
            @PathParam("id") String id,
            @HeaderParam("Authorization") String authorization
    );

@GET
    @Path("/users/{id}/role-mappings/realm")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<List<KeycloakRoleRepresentation>> getUserRoles(
            @PathParam("realm") String realm,
            @PathParam("id") String id,
            @HeaderParam("Authorization") String authorization
    );

    @POST
    @Path("/users/{id}/role-mappings/realm")
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Void> assignRoles(
            @PathParam("realm") String realm,
            @PathParam("id") String id,
            @HeaderParam("Authorization") String authorization,
            List<KeycloakRoleRepresentation> roles
    );

    @DELETE
    @Path("/users/{id}/role-mappings/realm")
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Void> removeRoles(
            @PathParam("realm") String realm,
            @PathParam("id") String id,
            @HeaderParam("Authorization") String authorization,
            List<KeycloakRoleRepresentation> roles
    );

    @GET
    @Path("/roles/{roleName}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<KeycloakRoleRepresentation> getRoleByName(
            @PathParam("realm") String realm,
            @PathParam("roleName") String roleName,
            @HeaderParam("Authorization") String authorization
    );

    @JsonIgnoreProperties(ignoreUnknown = true)
    record KeycloakUserRepresentation(
            String id,
            String username,
            String email,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            boolean enabled,
            List<Map<String, Object>> credentials
    ) {
        public static KeycloakUserRepresentation of(String username, String email,
                String firstName, String lastName, String password) {
            var cred = Map.<String, Object>of(
                    "type", "password",
                    "value", password,
                    "temporary", false
            );
            return new KeycloakUserRepresentation(null, username, email,
                    firstName, lastName, true, List.of(cred));
        }

        public static KeycloakUserRepresentation forUpdate(String username, String email,
                String firstName, String lastName) {
            return new KeycloakUserRepresentation(null, username, email,
                    firstName, lastName, true, null);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record KeycloakRoleRepresentation(String id, String name) {}
}
