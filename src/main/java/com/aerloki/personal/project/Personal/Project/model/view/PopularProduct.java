package com.aerloki.personal.project.Personal.Project.model.view;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * Materialized View Entity for Popular Products
 * Read-only entity mapped to mv_popular_products
 */
@Entity
@Immutable
@Table(name = "mv_popular_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularProduct {
    
    @Id
    private Long id;
    
    @Column(name = "asin")
    private String asin;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "order_count")
    private Long orderCount;
    
    @Column(name = "total_quantity_sold")
    private Long totalQuantitySold;
    
    @Column(name = "avg_order_value")
    private BigDecimal avgOrderValue;
}
