package br.com.domain.repository;

import br.com.domain.model.Customer;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface CustomerRepository {
    Uni<Customer> findById(String customerId);
    Uni<List<Customer>> findAll(int page, int size);
    Uni<List<Customer>> findByState(String state, int page, int size);
    Uni<Customer> save(Customer customer);
    Uni<Customer> update(Customer customer);
    Uni<Boolean> deleteById(String customerId);
    Uni<Long> count();
}
