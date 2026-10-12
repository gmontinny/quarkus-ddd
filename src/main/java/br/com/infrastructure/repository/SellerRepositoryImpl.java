package br.com.infrastructure.repository;

import br.com.domain.model.Seller;
import br.com.domain.repository.SellerRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SellerRepositoryImpl implements SellerRepository, PanacheRepositoryBase<Seller, String> {

    @Override
    public Uni<Seller> findById(String sellerId) {
        return find("sellerId", sellerId).firstResult();
    }

    @Override
    public Uni<List<Seller>> findAll(int page, int size) {
        return findAll().page(page, size).list();
    }

    @Override
    public Uni<List<Seller>> findByState(String state, int page, int size) {
        return find("sellerState", state).page(page, size).list();
    }

    @Override
    public Uni<Seller> save(Seller seller) {
        return persist(seller);
    }

    @Override
    public Uni<Seller> update(Seller seller) {
        return findById(seller.sellerId)
                .onItem().ifNotNull().transformToUni(existing -> {
                    existing.sellerZipCodePrefix = seller.sellerZipCodePrefix;
                    existing.sellerCity = seller.sellerCity;
                    existing.sellerState = seller.sellerState;
                    return persist(existing);
                });
    }

    @Override
    public Uni<Boolean> deleteById(String sellerId) {
        return delete("sellerId", sellerId).map(count -> count > 0);
    }

    @Override
    public Uni<Long> count() {
        return count("from Seller");
    }
}
