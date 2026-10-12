package br.com.infrastructure.security;

import br.com.application.dto.request.UserRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.UserResponse;
import br.com.domain.exception.BusinessException;
import br.com.domain.exception.ResourceNotFoundException;
import br.com.infrastructure.security.KeycloakAdminClient.KeycloakRoleRepresentation;
import br.com.infrastructure.security.KeycloakAdminClient.KeycloakUserRepresentation;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class KeycloakUserService {

    @ConfigProperty(name = "keycloak.realm", defaultValue = "spring-webflux")
    String realm;

    @ConfigProperty(name = "keycloak.client-id", defaultValue = "spring-webflux-client")
    String clientId;

    @ConfigProperty(name = "keycloak.credentials.secret", defaultValue = "spring-webflux-secret")
    String clientSecret;

    @RestClient
    KeycloakTokenClient tokenClient;

    @RestClient
    KeycloakAdminClient adminClient;

    private Uni<String> adminToken() {
        return tokenClient.getToken(realm, "client_credentials", clientId, clientSecret, null, null)
                .map(r -> "Bearer " + r.accessToken())
                .onFailure().transform(e -> new BusinessException("Failed to obtain admin token: " + e.getMessage()));
    }

    public Uni<List<UserResponse>> findAll(int page, int size) {
        return adminToken().flatMap(token ->
                adminClient.listUsers(realm, token, page * size, size, null, null)
                        .flatMap(users -> enrichWithRoles(users, token)));
    }

    public Uni<UserResponse> findById(String id) {
        return adminToken().flatMap(token ->
                adminClient.getUser(realm, id, token)
                        .onItem().ifNull().failWith(() -> new ResourceNotFoundException("User not found: " + id))
                        .flatMap(user -> adminClient.getUserRoles(realm, id, token)
                                .map(roles -> toResponse(user, roles))));
    }

    public Uni<UserResponse> create(UserRequest request) {
        return adminToken().flatMap(token ->
                adminClient.createUser(realm,token,
                        KeycloakUserRepresentation.of(request.username(), request.email(),
                                request.firstName(), request.lastName(), request.password()))
                        .flatMap(v -> adminClient.listUsers(realm, token, 0, 1, request.username(), true))
                        .flatMap(users -> {
                            if (users.isEmpty()) throw new BusinessException("User creation failed");
                            var created = users.get(0);
                            var roleName = request.role() != null ? request.role() : "ROLE_USER";
                            return adminClient.getRoleByName(realm, roleName, token)
                                    .flatMap(role -> adminClient.assignRoles(realm, created.id(), token, List.of(role)))
                                    .flatMap(v -> adminClient.getUserRoles(realm, created.id(), token)
                                            .map(roles -> toResponse(created, roles)));
                        }));
    }

    public Uni<UserResponse> update(String id, UserRequest request) {
        return adminToken().flatMap(token ->
                adminClient.getUser(realm, id, token)
                        .onItem().ifNull().failWith(() -> new ResourceNotFoundException("User not found: " + id))
                        .flatMap(existing -> adminClient.updateUser(realm, id, token,
                                KeycloakUserRepresentation.forUpdate(request.username(), request.email(),
                                        request.firstName(), request.lastName()))
                                .flatMap(v -> {
                                    if (request.role() == null) {
                                        return adminClient.getUserRoles(realm, id, token)
                                                .map(roles -> toResponse(existing, roles));
                                    }
                                    return adminClient.getUserRoles(realm, id, token)
                                            .flatMap(currentRoles -> {
                                                var realmRoles = currentRoles.stream()
                                                        .filter(r -> r.name().startsWith("ROLE_"))
                                                        .toList();
                                                return adminClient.removeRoles(realm, id, token, realmRoles)
                                                        .flatMap(v2 -> adminClient.getRoleByName(realm, request.role(), token))
                                                        .flatMap(role -> adminClient.assignRoles(realm, id, token, List.of(role)))
                                                        .flatMap(v2 -> adminClient.getUserRoles(realm, id, token)
                                                                .map(roles -> toResponse(existing, roles)));
                                            });
                                })));
    }

    public Uni<Void> delete(String id) {
        return adminToken().flatMap(token ->
                adminClient.getUser(realm, id, token)
                        .onItem().ifNull().failWith(() -> new ResourceNotFoundException("User not found: " + id))
                        .flatMap(u -> adminClient.deleteUser(realm, id, token)));
    }

    private Uni<List<UserResponse>> enrichWithRoles(List<KeycloakUserRepresentation> users, String token) {
        if (users.isEmpty()) return Uni.createFrom().item(List.of());
        var unis = users.stream()
                .map(u -> adminClient.getUserRoles(realm, u.id(), token)
                        .map(roles -> toResponse(u, roles)))
                .toList();
        return Uni.combine().all().unis(unis).with(list ->
                list.stream().map(o -> (UserResponse) o).toList());
    }

    private UserResponse toResponse(KeycloakUserRepresentation u, List<KeycloakRoleRepresentation> roles) {
        var roleNames = roles.stream().map(KeycloakRoleRepresentation::name)
                .filter(n -> n.startsWith("ROLE_")).toList();
        return new UserResponse(
                u.id(), u.username(), u.email(), u.firstName(), u.lastName(), u.enabled(),
                roleNames,
                List.of(
                        Link.self("/api/v1/users/" + u.id()),
                        Link.of("update", "/api/v1/users/" + u.id(), "PUT"),
                        Link.of("delete", "/api/v1/users/" + u.id(), "DELETE")
                )
        );
    }
}
