package br.com.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Olist DDD API",
                version = "1.0.0",
                description = "Quarkus DDD API — Keycloak, JWT, Vault, Reactive PostgreSQL, HATEOAS Level 3",
                contact = @Contact(name = "Olist Team", email = "api@olist.com")
        ),
        servers = @Server(url = "http://localhost:8081"),
        security = @org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        securitySchemeName = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@ApplicationScoped
public class OpenApiConfig {}
