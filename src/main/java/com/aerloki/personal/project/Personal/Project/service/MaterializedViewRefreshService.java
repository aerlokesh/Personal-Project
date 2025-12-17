package com.aerloki.personal.project.Personal.Project.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage refreshing of materialized views
 * Provides both scheduled and on-demand refresh capabilities
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MaterializedViewRefreshService {
    
    @PersistenceContext
    private final EntityManager entityManager;
    
    /**
     * Refresh all materialized views
     * Scheduled to run every hour
     */
    @Scheduled(cron = "0 0 * * * *")  // Every hour
    @Transactional
    public void refreshAllViews() {
        log.info("Starting scheduled refresh of all materialized views");
        long startTime = System.currentTimeMillis();
        
        try {
            refreshProductCatalogSummary();
            refreshUserOrderSummary();
            refreshPopularProducts();
            refreshCategoryStatistics();
            refreshOrderStatistics();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully refreshed all materialized views in {} ms", duration);
        } catch (Exception e) {
            log.error("Error refreshing materialized views", e);
            throw e;
        }
    }
    
    /**
     * Refresh product catalog summary materialized view
     */
    @Transactional
    public void refreshProductCatalogSummary() {
        log.debug("Refreshing mv_product_catalog_summary");
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_product_catalog_summary").executeUpdate();
    }
    
    /**
     * Refresh user order summary materialized view
     */
    @Transactional
    public void refreshUserOrderSummary() {
        log.debug("Refreshing mv_user_order_summary");
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_order_summary").executeUpdate();
    }
    
    /**
     * Refresh popular products materialized view
     */
    @Transactional
    public void refreshPopularProducts() {
        log.debug("Refreshing mv_popular_products");
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_popular_products").executeUpdate();
    }
    
    /**
     * Refresh category statistics materialized view
     */
    @Transactional
    public void refreshCategoryStatistics() {
        log.debug("Refreshing mv_category_statistics");
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_category_statistics").executeUpdate();
    }
    
    /**
     * Refresh order statistics materialized view
     */
    @Transactional
    public void refreshOrderStatistics() {
        log.debug("Refreshing mv_order_statistics");
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_order_statistics").executeUpdate();
    }
    
    /**
     * Refresh materialized views when order is placed
     * Called by OrderService after successful order placement
     */
    @Transactional
    public void refreshAfterOrderPlaced() {
        log.info("Refreshing materialized views after order placed");
        refreshProductCatalogSummary();
        refreshUserOrderSummary();
        refreshPopularProducts();
        refreshCategoryStatistics();
        refreshOrderStatistics();
    }
    
    /**
     * Refresh materialized views when product inventory is updated
     * Called after product stock changes
     */
    @Transactional
    public void refreshAfterInventoryUpdate() {
        log.info("Refreshing materialized views after inventory update");
        refreshProductCatalogSummary();
        refreshCategoryStatistics();
    }
}
