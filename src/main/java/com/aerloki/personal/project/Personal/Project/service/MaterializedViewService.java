package com.aerloki.personal.project.Personal.Project.service;

import com.aerloki.personal.project.Personal.Project.model.view.*;
import com.aerloki.personal.project.Personal.Project.repository.view.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for accessing materialized views
 * Provides fast access to pre-aggregated data for frontend display
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterializedViewService {
    
    private final ProductCatalogSummaryRepository catalogRepository;
    private final UserOrderSummaryRepository userOrderRepository;
    private final PopularProductRepository popularProductRepository;
    private final CategoryStatisticsRepository categoryStatsRepository;
    
    /**
     * Get top N popular products for homepage
     * Much faster than querying orders table with complex JOINs
     */
    public List<PopularProduct> getPopularProducts(int limit) {
        log.debug("Fetching top {} popular products from materialized view", limit);
        List<PopularProduct> products = popularProductRepository.findAllOrderedByPopularity();
        return products.stream().limit(limit).toList();
    }
    
    /**
     * Get popular products by category
     */
    public List<PopularProduct> getPopularProductsByCategory(String category, int limit) {
        log.debug("Fetching popular products for category: {}", category);
        List<PopularProduct> products = popularProductRepository.findByCategoryOrderedByPopularity(category);
        return products.stream().limit(limit).toList();
    }
    
    /**
     * Get product catalog with sales data
     * Shows how many times each product was sold
     */
    public List<ProductCatalogSummary> getProductCatalog() {
        log.debug("Fetching product catalog from materialized view");
        return catalogRepository.findAll();
    }
    
    /**
     * Get products by category with sales metrics
     */
    public List<ProductCatalogSummary> getProductsByCategory(String category) {
        log.debug("Fetching products for category: {}", category);
        return catalogRepository.findByCategory(category);
    }
    
    /**
     * Get available products with sales data
     */
    public List<ProductCatalogSummary> getAvailableProducts() {
        log.debug("Fetching available products from materialized view");
        return catalogRepository.findAvailableProducts();
    }
    
    /**
     * Get top selling products
     */
    public List<ProductCatalogSummary> getTopSellingProducts(int limit) {
        log.debug("Fetching top {} selling products", limit);
        List<ProductCatalogSummary> products = catalogRepository.findTopSellingProducts();
        return products.stream().limit(limit).toList();
    }
    
    /**
     * Get user order summary for dashboard
     * Shows total orders, spending, and order status breakdown
     */
    public Optional<UserOrderSummary> getUserOrderSummary(Long userId) {
        log.debug("Fetching order summary for user: {}", userId);
        return userOrderRepository.findByUserId(userId);
    }
    
    /**
     * Get user order summary by email
     */
    public Optional<UserOrderSummary> getUserOrderSummaryByEmail(String email) {
        log.debug("Fetching order summary for email: {}", email);
        return userOrderRepository.findByEmail(email);
    }
    
    /**
     * Get all category statistics
     * Shows product counts, price ranges, and sales data per category
     */
    public List<CategoryStatistics> getAllCategoryStatistics() {
        log.debug("Fetching all category statistics");
        return categoryStatsRepository.findAll();
    }
    
    /**
     * Get statistics for a specific category
     */
    public Optional<CategoryStatistics> getCategoryStatistics(String category) {
        log.debug("Fetching statistics for category: {}", category);
        return categoryStatsRepository.findByCategory(category);
    }
    
    /**
     * Get top categories by sales
     */
    public List<CategoryStatistics> getTopCategories(int limit) {
        log.debug("Fetching top {} categories by sales", limit);
        List<CategoryStatistics> categories = categoryStatsRepository.findAllOrderedByTotalSold();
        return categories.stream().limit(limit).toList();
    }
}
