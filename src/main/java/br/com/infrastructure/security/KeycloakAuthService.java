package br.com.infrastructure.security;

import br.com.application.dto.request.LoginRequest;
import br.com.application.dto.response.TokenResponse;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class KeycloakAuthService {

    @ConfigProperty(name = "keycloak.realm", defaultValue = "spring-webflux")
    String realm;

    @ConfigProperty(name = "keycloak.client-id", defaultValue = "spring-webflux-client")
    String clientId;

    @ConfigProperty(name = "keycloak.credentials.secret", defaultValue = "spring-webflux-secret")
    String clientSecret;

    @RestClient
    KeycloakTokenClient tokenClient;

    public Uni<TokenResponse> login(LoginRequest request) {
        return tokenClient.getToken(realm, "password", clientId, clientSecret,
                request.username(), request.password())
                .map(r -> TokenResponse.of(r.accessToken(), r.refreshToken(), r.expiresIn()));
    }

    public Uni<TokenResponse> refresh(String refreshToken) {
        return tokenClient.refreshToken(realm, "refresh_token", clientId, clientSecret, refreshToken)
                .map(r -> TokenResponse.of(r.accessToken(), r.refreshToken(), r.expiresIn()));
    }

    public Uni<Void> logout(String refreshToken) {
        return tokenClient.logout(realm, clientId, clientSecret, refreshToken);
    }
}
