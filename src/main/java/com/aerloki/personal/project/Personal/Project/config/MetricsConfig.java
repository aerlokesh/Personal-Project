package com.aerloki.personal.project.Personal.Project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aerloki.personal.project.Personal.Project.service.CacheMetricsService;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfig {
    
    @Bean
    public CacheMetricsService cacheMetricsService(MeterRegistry registry) {
        CacheMetricsService metricsService = new CacheMetricsService(registry);
        
        // Register gauges that Prometheus can scrape
        Gauge.builder("cache.hits", metricsService, CacheMetricsService::getTotalCacheHits)
            .description("Total cache hits")
            .register(registry);
            
        Gauge.builder("cache.misses", metricsService, CacheMetricsService::getTotalCacheMisses)
            .description("Total cache misses (database queries)")
            .register(registry);
            
        Gauge.builder("cache.hit.rate", metricsService, CacheMetricsService::getCacheHitRate)
            .description("Cache hit rate percentage")
            .register(registry);
        
        return metricsService;
    }
}
