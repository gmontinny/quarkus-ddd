package br.com.domain.repository;

import br.com.domain.model.Seller;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface SellerRepository {
    Uni<Seller> findById(String sellerId);
    Uni<List<Seller>> findAll(int page, int size);
    Uni<List<Seller>> findByState(String state, int page, int size);
    Uni<Seller> save(Seller seller);
    Uni<Seller> update(Seller seller);
    Uni<Boolean> deleteById(String sellerId);
    Uni<Long> count();
}
