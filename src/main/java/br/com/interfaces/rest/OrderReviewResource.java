package br.com.interfaces.rest;

import br.com.application.dto.request.OrderReviewRequest;
import br.com.application.dto.response.OrderReviewResponse;
import br.com.application.service.OrderReviewService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.net.URI;
import java.util.List;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Order Reviews", description = "Reviews of orders")
public class OrderReviewResource {

    @Inject OrderReviewService service;

    @GET
    @Path("/orders/{orderId}/reviews")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "List all reviews of an order")
    public Uni<List<OrderReviewResponse>> findByOrderId(@PathParam("orderId") String orderId) {
        return service.findByOrderId(orderId);
    }

    @GET
    @Path("/reviews/{reviewId}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Get review by ID")
    public Uni<OrderReviewResponse> findById(@PathParam("reviewId") String reviewId) {
        return service.findById(reviewId);
    }

    @POST
    @Path("/reviews")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Create a new review")
    public Uni<Response> create(@Valid OrderReviewRequest request) {
        return service.create(request)
                .map(r -> Response.created(URI.create("/api/v1/reviews/" + r.reviewId())).entity(r).build());
    }

    @PUT
    @Path("/reviews/{reviewId}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Update a review")
    public Uni<OrderReviewResponse> update(@PathParam("reviewId") String reviewId, @Valid OrderReviewRequest request) {
        return service.update(reviewId, request);
    }

    @DELETE
    @Path("/reviews/{reviewId}")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(summary = "Delete a review")
    public Uni<Response> delete(@PathParam("reviewId") String reviewId) {
        return service.delete(reviewId).map(v -> Response.noContent().build());
    }
}
