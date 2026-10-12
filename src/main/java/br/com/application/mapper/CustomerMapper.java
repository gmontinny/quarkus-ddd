package br.com.application.mapper;

import br.com.application.dto.request.CustomerRequest;
import br.com.application.dto.response.CustomerResponse;
import br.com.application.dto.response.Link;
import br.com.domain.model.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CustomerMapper {

    public Customer toDomain(CustomerRequest req) {
        var c = new Customer();
        c.customerId = req.customerId();
        c.customerUniqueId = req.customerUniqueId();
        c.customerZipCodePrefix = req.customerZipCodePrefix();
        c.customerCity = req.customerCity();
        c.customerState = req.customerState();
        return c;
    }

    public CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.customerId, c.customerUniqueId, c.customerZipCodePrefix,
                c.customerCity, c.customerState,
                buildLinks(c.customerId)
        );
    }

    private List<Link> buildLinks(String id) {
        return List.of(
                Link.self("/api/v1/customers/" + id),
                Link.of("orders", "/api/v1/orders?customerId=" + id, "GET"),
                Link.of("update", "/api/v1/customers/" + id, "PUT"),
                Link.of("delete", "/api/v1/customers/" + id, "DELETE")
        );
    }
}
