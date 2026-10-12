package br.com.interfaces.rest;

import br.com.application.dto.response.GeolocationResponse;
import br.com.application.dto.response.PageResponse;
import br.com.application.service.GeolocationService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/geolocation")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Geolocation", description = "Geolocation data by zip code or state")
public class GeolocationResource {

    @Inject GeolocationService service;

    @GET
    @Path("/zip/{zipCode}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Find geolocation by zip code prefix")
    public Uni<PageResponse<GeolocationResponse>> findByZipCode(
            @PathParam("zipCode") String zipCode,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.findByZipCode(zipCode, page, size);
    }

    @GET
    @Path("/state/{state}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Find geolocation by state")
    public Uni<PageResponse<GeolocationResponse>> findByState(
            @PathParam("state") String state,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.findByState(state, page, size);
    }
}
