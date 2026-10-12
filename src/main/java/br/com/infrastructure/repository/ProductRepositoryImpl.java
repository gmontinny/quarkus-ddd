package br.com.infrastructure.repository;

import br.com.domain.model.Product;
import br.com.domain.repository.ProductRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductRepositoryImpl implements ProductRepository, PanacheRepositoryBase<Product, String> {

    @Override
    public Uni<Product> findById(String productId) {
        return find("productId", productId).firstResult();
    }

    @Override
    public Uni<List<Product>> findAll(int page, int size) {
        return findAll().page(page, size).list();
    }

    @Override
    public Uni<List<Product>> findByCategory(String category, int page, int size) {
        return find("productCategoryName", category).page(page, size).list();
    }

    @Override
    public Uni<Product> save(Product product) {
        return persist(product);
    }

    @Override
    public Uni<Product> update(Product product) {
        return findById(product.productId)
                .onItem().ifNotNull().transformToUni(existing -> {
                    existing.productCategoryName = product.productCategoryName;
                    existing.productNameLenght = product.productNameLenght;
                    existing.productDescriptionLenght = product.productDescriptionLenght;
                    existing.productPhotosQty = product.productPhotosQty;
                    existing.productWeightG = product.productWeightG;
                    existing.productLengthCm = product.productLengthCm;
                    existing.productHeightCm = product.productHeightCm;
                    existing.productWidthCm = product.productWidthCm;
                    return persist(existing);
                });
    }

    @Override
    public Uni<Boolean> deleteById(String productId) {
        return delete("productId", productId).map(count -> count > 0);
    }

    @Override
    public Uni<Long> count() {
        return count("from Product");
    }
}
