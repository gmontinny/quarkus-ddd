package br.com.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Olist DDD API",
                version = "1.0.0",
                description = "Quarkus DDD API — Keycloak, JWT, Vault, Reactive PostgreSQL, HATEOAS Level 3",
                contact = @Contact(name = "Olist Team", email = "api@olist.com")
        ),
        servers = @Server(url = "http://localhost:8081")
)
@ApplicationScoped
public class OpenApiConfig {}
