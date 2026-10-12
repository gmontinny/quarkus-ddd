package br.com.infrastructure.repository;

import br.com.domain.model.Customer;
import br.com.domain.repository.CustomerRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CustomerRepositoryImpl implements CustomerRepository, PanacheRepositoryBase<Customer, String> {

    @Override
    public Uni<Customer> findById(String customerId) {
        return find("customerId", customerId).firstResult();
    }

    @Override
    public Uni<List<Customer>> findAll(int page, int size) {
        return findAll().page(page, size).list();
    }

    @Override
    public Uni<List<Customer>> findByState(String state, int page, int size) {
        return find("customerState", state).page(page, size).list();
    }

    @Override
    public Uni<Customer> save(Customer customer) {
        return persist(customer);
    }

    @Override
    public Uni<Customer> update(Customer customer) {
        return findById(customer.customerId)
                .onItem().ifNotNull().transformToUni(existing -> {
                    existing.customerUniqueId = customer.customerUniqueId;
                    existing.customerZipCodePrefix = customer.customerZipCodePrefix;
                    existing.customerCity = customer.customerCity;
                    existing.customerState = customer.customerState;
                    return persist(existing);
                });
    }

    @Override
    public Uni<Boolean> deleteById(String customerId) {
        return delete("customerId", customerId).map(count -> count > 0);
    }

    @Override
    public Uni<Long> count() {
        return count("from Customer");
    }
}
