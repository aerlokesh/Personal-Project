package com.aerloki.personal.project.Personal.Project.repository.view;

import com.aerloki.personal.project.Personal.Project.model.view.ProductCatalogSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ProductCatalogSummary Materialized View
 * Provides fast read access to aggregated product catalog data
 */
@Repository
public interface ProductCatalogSummaryRepository extends JpaRepository<ProductCatalogSummary, Long> {
    
    /**
     * Find product catalog summary by ASIN
     */
    @Query("SELECT p FROM ProductCatalogSummary p WHERE p.asin = :asin")
    Optional<ProductCatalogSummary> findByAsin(@Param("asin") String asin);
    
    /**
     * Find all products in a category
     */
    @Query("SELECT p FROM ProductCatalogSummary p WHERE p.category = :category ORDER BY p.name")
    List<ProductCatalogSummary> findByCategory(@Param("category") String category);
    
    /**
     * Find available products
     */
    @Query("SELECT p FROM ProductCatalogSummary p WHERE p.available = true ORDER BY p.name")
    List<ProductCatalogSummary> findAvailableProducts();
    
    /**
     * Find products by category and availability
     */
    @Query("SELECT p FROM ProductCatalogSummary p WHERE p.category = :category AND p.available = :available ORDER BY p.name")
    List<ProductCatalogSummary> findByCategoryAndAvailable(@Param("category") String category, @Param("available") Boolean available);
    
    /**
     * Find top selling products
     */
    @Query("SELECT p FROM ProductCatalogSummary p WHERE p.totalSold > 0 ORDER BY p.totalSold DESC")
    List<ProductCatalogSummary> findTopSellingProducts();
}
