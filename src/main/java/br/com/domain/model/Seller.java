package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller extends PanacheEntityBase {

    @Id
    @Column(name = "seller_id")
    public String sellerId;

    @Column(name = "seller_zip_code_prefix")
    public String sellerZipCodePrefix;

    @Column(name = "seller_city")
    public String sellerCity;

    @Column(name = "seller_state")
    public String sellerState;
}
