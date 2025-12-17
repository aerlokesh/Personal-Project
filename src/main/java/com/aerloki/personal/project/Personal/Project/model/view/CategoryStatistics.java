package com.aerloki.personal.project.Personal.Project.model.view;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * Materialized View Entity for Category Statistics
 * Read-only entity mapped to mv_category_statistics
 */
@Entity
@Immutable
@Table(name = "mv_category_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatistics {
    
    @Id
    @Column(name = "category")
    private String category;
    
    @Column(name = "product_count")
    private Long productCount;
    
    @Column(name = "available_products")
    private Long availableProducts;
    
    @Column(name = "avg_price")
    private BigDecimal avgPrice;
    
    @Column(name = "min_price")
    private BigDecimal minPrice;
    
    @Column(name = "max_price")
    private BigDecimal maxPrice;
    
    @Column(name = "total_sold")
    private Long totalSold;
    
    @Column(name = "total_orders")
    private Long totalOrders;
}
