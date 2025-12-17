package com.aerloki.personal.project.Personal.Project.model.view;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * Materialized View Entity for Product Catalog Summary
 * Read-only entity mapped to mv_product_catalog_summary
 */
@Entity
@Immutable
@Table(name = "mv_product_catalog_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCatalogSummary {
    
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
    
    @Column(name = "available")
    private Boolean available;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "stock")
    private Integer stock;
    
    @Column(name = "total_orders")
    private Long totalOrders;
    
    @Column(name = "total_sold")
    private Long totalSold;
}
