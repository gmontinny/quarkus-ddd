package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends PanacheEntityBase {

    @Id
    @Column(name = "customer_id")
    public String customerId;

    @Column(name = "customer_unique_id", nullable = false)
    public String customerUniqueId;

    @Column(name = "customer_zip_code_prefix")
    public String customerZipCodePrefix;

    @Column(name = "customer_city")
    public String customerCity;

    @Column(name = "customer_state")
    public String customerState;
}
