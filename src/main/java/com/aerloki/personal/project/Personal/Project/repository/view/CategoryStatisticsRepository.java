package com.aerloki.personal.project.Personal.Project.repository.view;

import com.aerloki.personal.project.Personal.Project.model.view.CategoryStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CategoryStatistics Materialized View
 * Provides fast read access to category-level metrics
 */
@Repository
public interface CategoryStatisticsRepository extends JpaRepository<CategoryStatistics, String> {
    
    /**
     * Find statistics for a specific category
     */
    @Query("SELECT c FROM CategoryStatistics c WHERE c.category = :category")
    Optional<CategoryStatistics> findByCategory(@Param("category") String category);
    
    /**
     * Find all categories ordered by total sales
     */
    @Query("SELECT c FROM CategoryStatistics c ORDER BY c.totalSold DESC")
    List<CategoryStatistics> findAllOrderedByTotalSold();
    
    /**
     * Find all categories ordered by product count
     */
    @Query("SELECT c FROM CategoryStatistics c ORDER BY c.productCount DESC")
    List<CategoryStatistics> findAllOrderedByProductCount();
}
