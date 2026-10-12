package br.com.application.service;

import br.com.application.dto.request.LoginRequest;
import br.com.application.dto.request.RefreshTokenRequest;
import br.com.application.dto.response.TokenResponse;
import br.com.domain.exception.BusinessException;
import br.com.infrastructure.security.KeycloakAuthService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject KeycloakAuthService keycloakAuthService;

    public Uni<TokenResponse> login(LoginRequest request) {
        return keycloakAuthService.login(request)
                .onFailure().transform(e -> new BusinessException("Invalid credentials: " + e.getMessage()));
    }

    public Uni<TokenResponse> refresh(RefreshTokenRequest request) {
        return keycloakAuthService.refresh(request.refreshToken())
                .onFailure().transform(e -> new BusinessException("Invalid or expired refresh token"));
    }

    public Uni<Void> logout(String refreshToken) {
        return keycloakAuthService.logout(refreshToken);
    }
}
