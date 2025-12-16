package com.aerloki.personal.project.Personal.Project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerloki.personal.project.Personal.Project.service.CacheMetricsService;

import lombok.RequiredArgsConstructor;

/**
 * REST endpoint to view cache metrics in real-time
 */
@RestController
@RequestMapping("/api/cache-metrics")
@RequiredArgsConstructor
public class CacheMetricsController {
    
    private final CacheMetricsService metricsService;
    
    /**
     * Get current cache statistics
     * Access at: http://localhost:8081/api/cache-metrics
     */
    @GetMapping
    public Map<String, Object> getCacheMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("totalCacheHits", metricsService.getTotalCacheHits());
        metrics.put("totalCacheMisses", metricsService.getTotalCacheMisses());
        metrics.put("cacheHitRate", String.format("%.2f%%", metricsService.getCacheHitRate()));
        metrics.put("summary", metricsService.getStatsSummary());
        
        // Calculate database savings
        long hits = metricsService.getTotalCacheHits();
        long misses = metricsService.getTotalCacheMisses();
        long totalRequests = hits + misses;
        
        metrics.put("totalRequests", totalRequests);
        metrics.put("databaseQueriesAvoided", hits);
        metrics.put("databaseQueriesExecuted", misses);
        
        if (totalRequests > 0) {
            double savingsPercent = ((double) hits / totalRequests) * 100;
            metrics.put("databaseLoadReduction", String.format("%.2f%%", savingsPercent));
        } else {
            metrics.put("databaseLoadReduction", "0.00%");
        }
        
        return metrics;
    }
}
