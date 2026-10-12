package br.com.application.mapper;

import br.com.application.dto.request.SellerRequest;
import br.com.application.dto.response.Link;
import br.com.application.dto.response.SellerResponse;
import br.com.domain.model.Seller;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SellerMapper {

    public Seller toDomain(SellerRequest req) {
        var s = new Seller();
        s.sellerId = req.sellerId();
        s.sellerZipCodePrefix = req.sellerZipCodePrefix();
        s.sellerCity = req.sellerCity();
        s.sellerState = req.sellerState();
        return s;
    }

    public SellerResponse toResponse(Seller s) {
        return new SellerResponse(
                s.sellerId, s.sellerZipCodePrefix, s.sellerCity, s.sellerState,
                buildLinks(s.sellerId)
        );
    }

    private List<Link> buildLinks(String id) {
        return List.of(
                Link.self("/api/v1/sellers/" + id),
                Link.of("update", "/api/v1/sellers/" + id, "PUT"),
                Link.of("delete", "/api/v1/sellers/" + id, "DELETE")
        );
    }
}
