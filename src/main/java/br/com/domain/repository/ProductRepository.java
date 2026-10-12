package br.com.domain.repository;

import br.com.domain.model.Product;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface ProductRepository {
    Uni<Product> findById(String productId);
    Uni<List<Product>> findAll(int page, int size);
    Uni<List<Product>> findByCategory(String category, int page, int size);
    Uni<Product> save(Product product);
    Uni<Product> update(Product product);
    Uni<Boolean> deleteById(String productId);
    Uni<Long> count();
}
