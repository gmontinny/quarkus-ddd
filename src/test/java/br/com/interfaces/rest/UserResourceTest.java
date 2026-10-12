package br.com.interfaces.rest;

import br.com.application.dto.response.Link;
import br.com.application.dto.response.UserResponse;
import br.com.infrastructure.security.KeycloakUserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class UserResourceTest {

    @InjectMock
    KeycloakUserService userService;

    private UserResponse sampleUser() {
        return new UserResponse(
                "user-id-001", "johndoe", "john@example.com",
                "John", "Doe", true,
                List.of("ROLE_USER"),
                List.of(Link.self("/api/v1/users/user-id-001"))
        );
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testFindAll() {
        Mockito.when(userService.findAll(0, 20))
                .thenReturn(Uni.createFrom().item(List.of(sampleUser())));

        given()
                .when().get("/api/v1/users")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].username", is("johndoe"))
                .body("[0].roles", hasItem("ROLE_USER"));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testFindById() {
        Mockito.when(userService.findById("user-id-001"))
                .thenReturn(Uni.createFrom().item(sampleUser()));

        given()
                .when().get("/api/v1/users/user-id-001")
                .then()
                .statusCode(200)
                .body("id", is("user-id-001"))
                .body("username", is("johndoe"))
                .body("email", is("john@example.com"));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testCreate() {
        Mockito.when(userService.create(Mockito.any()))
                .thenReturn(Uni.createFrom().item(sampleUser()));

        given()
                .contentType("application/json")
                .body("""
                        {"username":"johndoe","email":"john@example.com",
                         "firstName":"John","lastName":"Doe",
                         "password":"pass123","role":"ROLE_USER"}
                        """)
                .when().post("/api/v1/users")
                .then()
                .statusCode(201)
                .body("username", is("johndoe"));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testDelete() {
        Mockito.when(userService.delete("user-id-001"))
                .thenReturn(Uni.createFrom().voidItem());

        given()
                .when().delete("/api/v1/users/user-id-001")
                .then()
                .statusCode(204);
    }

    @Test
    void testUnauthorized() {
        given()
                .when().get("/api/v1/users")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "manager", roles = {"ROLE_MANAGER"})
    void testForbiddenForManager() {
        given()
                .when().get("/api/v1/users")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testCreateInvalidBody() {
        given()
                .contentType("application/json")
                .body("{}")
                .when().post("/api/v1/users")
                .then()
                .statusCode(400);
    }
}
