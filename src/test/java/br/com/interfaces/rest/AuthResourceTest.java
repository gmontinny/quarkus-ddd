package br.com.interfaces.rest;

import br.com.application.dto.response.TokenResponse;
import br.com.application.service.AuthService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AuthResourceTest {

    @InjectMock
    AuthService authService;

    @Test
    void testLogin_success() {
        var token = TokenResponse.of("access-token-123", "refresh-token-456", 300L);
        Mockito.when(authService.login(Mockito.any()))
                .thenReturn(Uni.createFrom().item(token));

        given()
                .contentType("application/json")
                .body("""
                        {"username":"user","password":"user123"}
                        """)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", is("access-token-123"))
                .body("refreshToken", is("refresh-token-456"))
                .body("tokenType", is("Bearer"))
                .body("expiresIn", is(300));
    }

    @Test
    void testLogin_invalidCredentials() {
        Mockito.when(authService.login(Mockito.any()))
                .thenReturn(Uni.createFrom().failure(
                        new br.com.domain.exception.BusinessException("Invalid credentials")));

        given()
                .contentType("application/json")
                .body("""
                        {"username":"user","password":"wrong"}
                        """)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(422);
    }

    @Test
    void testRefresh_success() {
        var token = TokenResponse.of("new-access-token", "new-refresh-token", 300L);
        Mockito.when(authService.refresh(Mockito.any()))
                .thenReturn(Uni.createFrom().item(token));

        given()
                .contentType("application/json")
                .body("""
                        {"refreshToken":"valid-refresh-token"}
                        """)
                .when().post("/api/v1/auth/refresh")
                .then()
                .statusCode(200)
                .body("accessToken", is("new-access-token"));
    }

    @Test
    void testLogin_missingFields() {
        given()
                .contentType("application/json")
                .body("{}")
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(400);
    }
}
