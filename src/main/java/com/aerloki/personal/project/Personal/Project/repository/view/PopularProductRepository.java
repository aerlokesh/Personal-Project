package com.aerloki.personal.project.Personal.Project.repository.view;

import com.aerloki.personal.project.Personal.Project.model.view.PopularProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PopularProduct Materialized View
 * Provides fast read access to trending products
 */
@Repository
public interface PopularProductRepository extends JpaRepository<PopularProduct, Long> {
    
    /**
     * Find all popular products ordered by order count
     */
    @Query("SELECT p FROM PopularProduct p ORDER BY p.orderCount DESC, p.totalQuantitySold DESC")
    List<PopularProduct> findAllOrderedByPopularity();
    
    /**
     * Find popular products by category
     */
    @Query("SELECT p FROM PopularProduct p WHERE p.category = :category ORDER BY p.orderCount DESC")
    List<PopularProduct> findByCategoryOrderedByPopularity(@Param("category") String category);
    
    /**
     * Find top N popular products
     */
    @Query(value = "SELECT p FROM PopularProduct p ORDER BY p.orderCount DESC, p.totalQuantitySold DESC")
    List<PopularProduct> findTopPopularProducts();
}
