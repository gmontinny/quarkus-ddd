package br.com.infrastructure.security;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "keycloak-token-client")
@Path("/realms/{realm}/protocol/openid-connect")
public interface KeycloakTokenClient {

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<KeycloakTokenResponse> getToken(
            @PathParam("realm") String realm,
            @FormParam("grant_type") String grantType,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("username") String username,
            @FormParam("password") String password
    );

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<KeycloakTokenResponse> refreshToken(
            @PathParam("realm") String realm,
            @FormParam("grant_type") String grantType,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("refresh_token") String refreshToken
    );

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Uni<Void> logout(
            @PathParam("realm") String realm,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("refresh_token") String refreshToken
    );
}
