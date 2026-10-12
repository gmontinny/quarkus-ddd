package br.com.application.mapper;

import br.com.application.dto.request.ProductRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.ProductResponse;
import br.com.domain.model.Product;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductMapper {

    public Product toDomain(ProductRequest req) {
        var p = new Product();
        p.productId = req.productId();
        p.productCategoryName = req.productCategoryName();
        p.productNameLenght = req.productNameLenght();
        p.productDescriptionLenght = req.productDescriptionLenght();
        p.productPhotosQty = req.productPhotosQty();
        p.productWeightG = req.productWeightG();
        p.productLengthCm = req.productLengthCm();
        p.productHeightCm = req.productHeightCm();
        p.productWidthCm = req.productWidthCm();
        return p;
    }

    public ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.productId, p.productCategoryName, p.productNameLenght,
                p.productDescriptionLenght, p.productPhotosQty, p.productWeightG,
                p.productLengthCm, p.productHeightCm, p.productWidthCm,
                buildLinks(p.productId)
        );
    }

    private List<Link> buildLinks(String id) {
        return List.of(
                Link.self("/api/v1/products/" + id),
                Link.of("update", "/api/v1/products/" + id, "PUT"),
                Link.of("delete", "/api/v1/products/" + id, "DELETE")
        );
    }
}
