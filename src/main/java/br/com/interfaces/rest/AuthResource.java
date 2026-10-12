package br.com.interfaces.rest;

import br.com.application.dto.request.LoginRequest;
import br.com.application.dto.request.RefreshTokenRequest;
import br.com.application.dto.response.TokenResponse;
import br.com.application.service.AuthService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Login, Refresh Token and Logout")
public class AuthResource {

    @Inject AuthService authService;

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Authenticate user and return JWT tokens")
    public Uni<Response> login(@Valid LoginRequest request) {
        return authService.login(request)
                .map(token -> Response.ok(token).build());
    }

    @POST
    @Path("/refresh")
    @PermitAll
    @Operation(summary = "Refresh access token using refresh token")
    public Uni<Response> refresh(@Valid RefreshTokenRequest request) {
        return authService.refresh(request)
                .map(token -> Response.ok(token).build());
    }

    @POST
    @Path("/logout")
    @Operation(summary = "Logout and revoke refresh token")
    public Uni<Response> logout(@Valid RefreshTokenRequest request) {
        return authService.logout(request.refreshToken())
                .map(v -> Response.noContent().build());
    }
}
