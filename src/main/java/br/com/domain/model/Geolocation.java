package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "geolocation")
public class Geolocation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "geolocation_zip_code_prefix")
    public String zipCodePrefix;

    @Column(name = "geolocation_lat")
    public Double lat;

    @Column(name = "geolocation_lng")
    public Double lng;

    @Column(name = "geolocation_city")
    public String city;

    @Column(name = "geolocation_state")
    public String state;
}
