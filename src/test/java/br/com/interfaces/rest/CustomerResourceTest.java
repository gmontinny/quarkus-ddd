package br.com.interfaces.rest;

import br.com.application.dto.response.CustomerResponse;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.PageResponse;
import br.com.application.service.CustomerService;
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
class CustomerResourceTest {

    @InjectMock
    CustomerService customerService;

    private CustomerResponse sampleCustomer() {
        return new CustomerResponse(
                "cust-001", "unique-001", "01310", "São Paulo", "SP",
                List.of(Link.self("/api/v1/customers/cust-001"))
        );
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"ROLE_USER"})
    void testFindAll() {
        var page = PageResponse.of(List.of(sampleCustomer()), 0, 20, 1L,
                List.of(Link.self("/api/v1/customers?page=0&size=20")));
        Mockito.when(customerService.findAll(0, 20)).thenReturn(Uni.createFrom().item(page));

        given()
                .when().get("/api/v1/customers")
                .then()
                .statusCode(200)
                .body("content.size()", is(1))
                .body("content[0].customerId", is("cust-001"))
                .body("content[0].links", notNullValue());
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"ROLE_USER"})
    void testFindById() {
        Mockito.when(customerService.findById("cust-001"))
                .thenReturn(Uni.createFrom().item(sampleCustomer()));

        given()
                .when().get("/api/v1/customers/cust-001")
                .then()
                .statusCode(200)
                .body("customerId", is("cust-001"))
                .body("customerState", is("SP"))
                .body("links", notNullValue());
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"ROLE_USER"})
    void testFindById_notFound() {
        Mockito.when(customerService.findById("not-exist"))
                .thenReturn(Uni.createFrom().failure(
                        new br.com.domain.exception.ResourceNotFoundException("Customer not found: not-exist")));

        given()
                .when().get("/api/v1/customers/not-exist")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testCreate() {
        Mockito.when(customerService.create(Mockito.any()))
                .thenReturn(Uni.createFrom().item(sampleCustomer()));

        given()
                .contentType("application/json")
                .body("""
                        {"customerId":"cust-001","customerUniqueId":"unique-001",
                         "customerZipCodePrefix":"01310","customerCity":"São Paulo","customerState":"SP"}
                        """)
                .when().post("/api/v1/customers")
                .then()
                .statusCode(201)
                .body("customerId", is("cust-001"));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testDelete() {
        Mockito.when(customerService.delete("cust-001"))
                .thenReturn(Uni.createFrom().voidItem());

        given()
                .when().delete("/api/v1/customers/cust-001")
                .then()
                .statusCode(204);
    }

    @Test
    void testUnauthorized() {
        given()
                .when().get("/api/v1/customers")
                .then()
                .statusCode(401);
    }
}
