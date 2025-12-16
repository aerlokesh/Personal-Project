package com.aerloki.personal.project.Personal.Project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerloki.personal.project.Personal.Project.service.CacheMetricsService;

import lombok.RequiredArgsConstructor;

/**
 * Custom Prometheus metrics endpoint
 */
@RestController
@RequiredArgsConstructor
public class PrometheusMetricsController {
    
    private final CacheMetricsService metricsService;
    
    /**
     * Export metrics in Prometheus format
     * Access at: http://localhost:8081/metrics/cache
     */
    @GetMapping(value = "/metrics/cache", produces = "text/plain")
    public String getPrometheusMetrics() {
        StringBuilder sb = new StringBuilder();
        
        // Cache Hits
        sb.append("# HELP cache_hits_total Total number of cache hits\n");
        sb.append("# TYPE cache_hits_total counter\n");
        sb.append("cache_hits_total ").append(metricsService.getTotalCacheHits()).append("\n");
        sb.append("\n");
        
        // Cache Misses (Database Queries)
        sb.append("# HELP cache_misses_total Total number of cache misses (database queries)\n");
        sb.append("# TYPE cache_misses_total counter\n");
        sb.append("cache_misses_total ").append(metricsService.getTotalCacheMisses()).append("\n");
        sb.append("\n");
        
        // Cache Hit Rate
        sb.append("# HELP cache_hit_rate Cache hit rate percentage\n");
        sb.append("# TYPE cache_hit_rate gauge\n");
        sb.append("cache_hit_rate ").append(metricsService.getCacheHitRate()).append("\n");
        sb.append("\n");
        
        // Database Queries Avoided
        long hits = metricsService.getTotalCacheHits();
        sb.append("# HELP database_queries_avoided Number of database queries avoided by caching\n");
        sb.append("# TYPE database_queries_avoided counter\n");
        sb.append("database_queries_avoided ").append(hits).append("\n");
        
        return sb.toString();
    }
}
