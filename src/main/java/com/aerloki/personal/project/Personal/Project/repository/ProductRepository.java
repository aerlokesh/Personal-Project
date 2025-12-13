package com.aerloki.personal.project.Personal.Project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aerloki.personal.project.Personal.Project.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find product by ASIN with optimized query
     */
    @Query("SELECT p FROM Product p WHERE p.asin = :asin")
    Optional<Product> findByAsin(@Param("asin") String asin);
    
    /**
     * Find products by category with optimized query
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category ORDER BY p.name")
    List<Product> findByCategory(@Param("category") String category);
    
    /**
     * Find available products with optimized query
     */
    @Query("SELECT p FROM Product p WHERE p.available = :available ORDER BY p.name")
    List<Product> findByAvailable(@Param("available") Boolean available);
    
    /**
     * Search products by name with optimized query
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.name")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find all products ordered by name for better caching
     */
    @Query("SELECT p FROM Product p ORDER BY p.name")
    List<Product> findAll();
}
