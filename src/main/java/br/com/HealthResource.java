package br.com;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/v1/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health", description = "Application health check")
public class HealthResource {

    @GET
    @PermitAll
    @Operation(summary = "Health check endpoint")
    public Response health() {
        return Response.ok(Map.of(
                "status", "UP",
                "application", "quarkus-ddd",
                "version", "1.0.0"
        )).build();
    }
}
