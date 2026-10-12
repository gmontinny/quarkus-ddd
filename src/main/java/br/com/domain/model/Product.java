package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product extends PanacheEntityBase {

    @Id
    @Column(name = "product_id")
    public String productId;

    @Column(name = "product_category_name")
    public String productCategoryName;

    @Column(name = "product_name_lenght")
    public Integer productNameLenght;

    @Column(name = "product_description_lenght")
    public Integer productDescriptionLenght;

    @Column(name = "product_photos_qty")
    public Integer productPhotosQty;

    @Column(name = "product_weight_g")
    public Integer productWeightG;

    @Column(name = "product_length_cm")
    public Integer productLengthCm;

    @Column(name = "product_height_cm")
    public Integer productHeightCm;

    @Column(name = "product_width_cm")
    public Integer productWidthCm;
}
