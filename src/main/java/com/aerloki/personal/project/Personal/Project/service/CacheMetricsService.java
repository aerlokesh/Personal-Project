package com.aerloki.personal.project.Personal.Project.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to track cache hits vs database hits for monitoring
 */
@Service
@Slf4j
public class CacheMetricsService {
    
    private final Counter userCacheHits;
    private final Counter userCacheMisses;
    private final Counter productCacheHits;
    private final Counter productCacheMisses;
    private final Counter allProductsCacheHits;
    private final Counter allProductsCacheMisses;
    
    // In-memory counters for easy access
    private final AtomicLong totalCacheHits = new AtomicLong(0);
    private final AtomicLong totalCacheMisses = new AtomicLong(0);
    
    public CacheMetricsService(MeterRegistry meterRegistry) {
        // User cache metrics
        this.userCacheHits = Counter.builder("cache.hits")
            .tag("type", "user")
            .description("Number of user cache hits")
            .register(meterRegistry);
            
        this.userCacheMisses = Counter.builder("cache.misses")
            .tag("type", "user")
            .description("Number of user cache misses (database queries)")
            .register(meterRegistry);
        
        // Product cache metrics
        this.productCacheHits = Counter.builder("cache.hits")
            .tag("type", "product")
            .description("Number of product cache hits")
            .register(meterRegistry);
            
        this.productCacheMisses = Counter.builder("cache.misses")
            .tag("type", "product")
            .description("Number of product cache misses (database queries)")
            .register(meterRegistry);
        
        // All products cache metrics
        this.allProductsCacheHits = Counter.builder("cache.hits")
            .tag("type", "all_products")
            .description("Number of all products cache hits")
            .register(meterRegistry);
            
        this.allProductsCacheMisses = Counter.builder("cache.misses")
            .tag("type", "all_products")
            .description("Number of all products cache misses (database queries)")
            .register(meterRegistry);
    }
    
    // User metrics
    public void recordUserCacheHit() {
        userCacheHits.increment();
        totalCacheHits.incrementAndGet();
        log.debug("📊 User cache hit recorded. Total hits: {}, Total misses: {}", 
            totalCacheHits.get(), totalCacheMisses.get());
    }
    
    public void recordUserCacheMiss() {
        userCacheMisses.increment();
        totalCacheMisses.incrementAndGet();
        log.debug("📊 User cache miss recorded. Total hits: {}, Total misses: {}", 
            totalCacheHits.get(), totalCacheMisses.get());
    }
    
    // Product metrics
    public void recordProductCacheHit() {
        productCacheHits.increment();
        totalCacheHits.incrementAndGet();
        log.debug("📊 Product cache hit recorded. Total hits: {}, Total misses: {}", 
            totalCacheHits.get(), totalCacheMisses.get());
    }
    
    public void recordProductCacheMiss() {
        productCacheMisses.increment();
        totalCacheMisses.incrementAndGet();
        log.debug("📊 Product cache miss recorded. Total hits: {}, Total misses: {}", 
            totalCacheHits.get(), totalCacheMisses.get());
    }
    
    // All products metrics
    public void recordAllProductsCacheHit() {
        allProductsCacheHits.increment();
        totalCacheHits.incrementAndGet();
        log.debug("📊 All products cache hit recorded. Total hits: {}, Total misses: {}", 
            totalCacheHits.get(), totalCacheMisses.get());
    }
    
    public void recordAllProductsCacheMiss() {
        allProductsCacheMisses.increment();
        totalCacheMisses.incrementAndGet();
        log.debug("📊 All products cache miss recorded. Total hits: {}, Total misses: {}", 
            totalCacheHits.get(), totalCacheMisses.get());
    }
    
    // Statistics
    public long getTotalCacheHits() {
        return totalCacheHits.get();
    }
    
    public long getTotalCacheMisses() {
        return totalCacheMisses.get();
    }
    
    public double getCacheHitRate() {
        long hits = totalCacheHits.get();
        long misses = totalCacheMisses.get();
        long total = hits + misses;
        
        if (total == 0) {
            return 0.0;
        }
        
        return (double) hits / total * 100.0;
    }
    
    public String getStatsSummary() {
        return String.format("Cache Stats: Hits=%d, Misses=%d, Hit Rate=%.2f%%", 
            getTotalCacheHits(), getTotalCacheMisses(), getCacheHitRate());
    }
}
