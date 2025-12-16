package com.aerloki.personal.project.Personal.Project.service;

import java.time.Duration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.aerloki.personal.project.Personal.Project.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserCacheService {
    private static final Logger log = LoggerFactory.getLogger(UserCacheService.class);
    private static final String USER_CACHE_PREFIX = "user:cache:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheMetricsService metricsService;
    
    public UserCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, 
                           CacheMetricsService metricsService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.metricsService = metricsService;
    }
    
    /**
     * Get user from cache
     */
    public Optional<User> getUserFromCache(String email) {
        String key = USER_CACHE_PREFIX + email;
        try {
            String cachedUser = (String) redisTemplate.opsForValue().get(key);
            if (cachedUser != null) {
                metricsService.recordUserCacheHit(); // 📊 Record cache hit
                log.info("✅ CACHE HIT: User found in Redis cache for email: {}", email);
                User user = objectMapper.readValue(cachedUser, User.class);
                return Optional.of(user);
            }
            metricsService.recordUserCacheMiss(); // 📊 Record cache miss (DB query coming)
            log.info("❌ CACHE MISS: User not found in Redis cache for email: {}", email);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error reading user from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Save user to cache
     */
    public void saveUserToCache(User user) {
        String key = USER_CACHE_PREFIX + user.getEmail();
        try {
            String userJson = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, userJson, CACHE_TTL);
            log.info("💾 CACHED: User saved to Redis cache for email: {} (TTL: {} minutes)", 
                user.getEmail(), CACHE_TTL.toMinutes());
        } catch (JsonProcessingException e) {
            log.error("Error saving user to cache: {}", e.getMessage());
        }
    }
    
    /**
     * Invalidate user cache
     */
    public void invalidateUserCache(String email) {
        String key = USER_CACHE_PREFIX + email;
        redisTemplate.delete(key);
        log.info("🗑️  INVALIDATED: User cache cleared for email: {}", email);
    }
    
    /**
     * Check if user exists in cache
     */
    public boolean isUserCached(String email) {
        String key = USER_CACHE_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
