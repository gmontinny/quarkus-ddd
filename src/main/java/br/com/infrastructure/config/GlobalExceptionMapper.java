package br.com.infrastructure.config;

import br.com.domain.exception.BusinessException;
import br.com.domain.exception.ResourceNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException ex) {
        if (ex instanceof ResourceNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Not Found", "message", ex.getMessage()))
                    .build();
        }
        if (ex instanceof BusinessException) {
            return Response.status(422)
                    .entity(Map.of("error", "Business Error", "message", ex.getMessage()))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Internal Server Error", "message", ex.getMessage()))
                .build();
    }
}
