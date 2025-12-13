package com.aerloki.personal.project.Personal.Project.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to manage user session data in Redis
 * Remembers last login email for improved user experience
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String LAST_LOGIN_PREFIX = "user:lastLogin:";
    private static final String USER_SESSION_PREFIX = "user:session:";
    private static final long SESSION_TTL_DAYS = 7;
    
    /**
     * Save the last login email for a user
     * @param email The email address that was used to login
     */
    public void saveLastLoginEmail(String email) {
        try {
            String key = LAST_LOGIN_PREFIX + "email";
            redisTemplate.opsForValue().set(key, email, SESSION_TTL_DAYS, TimeUnit.DAYS);
            log.info("Saved last login email to Redis: {}", email);
        } catch (Exception e) {
            log.error("Error saving last login email to Redis", e);
        }
    }
    
    /**
     * Get the last login email
     * @return The last email used to login, or null if not found
     */
    public String getLastLoginEmail() {
        try {
            String key = LAST_LOGIN_PREFIX + "email";
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("Error retrieving last login email from Redis", e);
            return null;
        }
    }
    
    /**
     * Save user session data
     * @param userId The user ID
     * @param sessionData The session data to store
     */
    public void saveUserSession(Long userId, String sessionData) {
        try {
            String key = USER_SESSION_PREFIX + userId;
            redisTemplate.opsForValue().set(key, sessionData, SESSION_TTL_DAYS, TimeUnit.DAYS);
            log.info("Saved session data for user: {}", userId);
        } catch (Exception e) {
            log.error("Error saving user session to Redis", e);
        }
    }
    
    /**
     * Get user session data
     * @param userId The user ID
     * @return The session data, or null if not found
     */
    public String getUserSession(Long userId) {
        try {
            String key = USER_SESSION_PREFIX + userId;
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.error("Error retrieving user session from Redis", e);
            return null;
        }
    }
    
    /**
     * Clear user session
     * @param userId The user ID
     */
    public void clearUserSession(Long userId) {
        try {
            String key = USER_SESSION_PREFIX + userId;
            redisTemplate.delete(key);
            log.info("Cleared session for user: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing user session from Redis", e);
        }
    }
}
