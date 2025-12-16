package com.aerloki.personal.project.Personal.Project.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.aerloki.personal.project.Personal.Project.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductCacheService {
    private static final Logger log = LoggerFactory.getLogger(ProductCacheService.class);
    private static final String PRODUCT_CACHE_PREFIX = "product:cache:";
    private static final String ALL_PRODUCTS_KEY = "products:all";
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheMetricsService metricsService;
    
    public ProductCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                              CacheMetricsService metricsService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.metricsService = metricsService;
    }
    
    /**
     * Get product from cache by ID
     */
    public Optional<Product> getProductFromCache(Long productId) {
        String key = PRODUCT_CACHE_PREFIX + productId;
        try {
            String cachedProduct = (String) redisTemplate.opsForValue().get(key);
            if (cachedProduct != null) {
                metricsService.recordProductCacheHit(); // 📊 Record cache hit
                log.info("✅ CACHE HIT: Product ID {} found in Redis cache", productId);
                Product product = objectMapper.readValue(cachedProduct, Product.class);
                return Optional.of(product);
            }
            metricsService.recordProductCacheMiss(); // 📊 Record cache miss (DB query coming)
            log.info("❌ CACHE MISS: Product ID {} not found in Redis cache", productId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error reading product from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Get all products from cache
     */
    public Optional<List<Product>> getAllProductsFromCache() {
        try {
            String cachedProducts = (String) redisTemplate.opsForValue().get(ALL_PRODUCTS_KEY);
            if (cachedProducts != null) {
                metricsService.recordAllProductsCacheHit(); // 📊 Record cache hit
                log.info("✅ CACHE HIT: All products found in Redis cache");
                List<Product> products = objectMapper.readValue(cachedProducts, 
                    new TypeReference<List<Product>>() {});
                return Optional.of(products);
            }
            metricsService.recordAllProductsCacheMiss(); // 📊 Record cache miss (DB query coming)
            log.info("❌ CACHE MISS: All products not found in Redis cache");
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error reading all products from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Save single product to cache
     */
    public void saveProductToCache(Product product) {
        String key = PRODUCT_CACHE_PREFIX + product.getId();
        try {
            String productJson = objectMapper.writeValueAsString(product);
            redisTemplate.opsForValue().set(key, productJson, CACHE_TTL);
            log.info("💾 CACHED: Product ID {} '{}' saved to Redis cache (TTL: {} minutes)", 
                product.getId(), product.getName(), CACHE_TTL.toMinutes());
        } catch (JsonProcessingException e) {
            log.error("Error saving product to cache: {}", e.getMessage());
        }
    }
    
    /**
     * Save list of products to cache
     */
    public void saveAllProductsToCache(List<Product> products) {
        try {
            String productsJson = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(ALL_PRODUCTS_KEY, productsJson, CACHE_TTL);
            log.info("💾 CACHED: {} products saved to Redis cache (TTL: {} minutes)", 
                products.size(), CACHE_TTL.toMinutes());
            
            // Also cache individual products
            products.forEach(this::saveProductToCache);
        } catch (JsonProcessingException e) {
            log.error("Error saving all products to cache: {}", e.getMessage());
        }
    }
    
    /**
     * Invalidate single product cache
     */
    public void invalidateProductCache(Long productId) {
        String key = PRODUCT_CACHE_PREFIX + productId;
        redisTemplate.delete(key);
        // Also invalidate all products cache since it changed
        redisTemplate.delete(ALL_PRODUCTS_KEY);
        log.info("🗑️  INVALIDATED: Product cache cleared for ID: {}", productId);
    }
    
    /**
     * Invalidate all products cache
     */
    public void invalidateAllProductsCache() {
        redisTemplate.delete(ALL_PRODUCTS_KEY);
        log.info("🗑️  INVALIDATED: All products cache cleared");
    }
}
