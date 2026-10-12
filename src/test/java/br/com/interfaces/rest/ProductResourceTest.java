package br.com.interfaces.rest;

import br.com.application.dto.response.Link;
import br.com.application.dto.response.PageResponse;
import br.com.application.dto.response.ProductResponse;
import br.com.application.service.ProductService;
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
class ProductResourceTest {

    @InjectMock
    ProductService productService;

    private ProductResponse sampleProduct() {
        return new ProductResponse(
                "prod-001", "perfumaria", 40, 287, 1, 225, 16, 10, 14,
                List.of(Link.self("/api/v1/products/prod-001"))
        );
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"ROLE_USER"})
    void testFindAll() {
        var page = PageResponse.of(List.of(sampleProduct()), 0, 20, 1L,
                List.of(Link.self("/api/v1/products?page=0&size=20")));
        Mockito.when(productService.findAll(0, 20)).thenReturn(Uni.createFrom().item(page));

        given()
                .when().get("/api/v1/products")
                .then()
                .statusCode(200)
                .body("content.size()", is(1))
                .body("content[0].productId", is("prod-001"))
                .body("content[0].links", notNullValue());
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"ROLE_USER"})
    void testFindById() {
        Mockito.when(productService.findById("prod-001"))
                .thenReturn(Uni.createFrom().item(sampleProduct()));

        given()
                .when().get("/api/v1/products/prod-001")
                .then()
                .statusCode(200)
                .body("productId", is("prod-001"))
                .body("productCategoryName", is("perfumaria"));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
    void testCreate() {
        Mockito.when(productService.create(Mockito.any()))
                .thenReturn(Uni.createFrom().item(sampleProduct()));

        given()
                .contentType("application/json")
                .body("""
                        {"productId":"prod-001","productCategoryName":"perfumaria"}
                        """)
                .when().post("/api/v1/products")
                .then()
                .statusCode(201);
    }
}
