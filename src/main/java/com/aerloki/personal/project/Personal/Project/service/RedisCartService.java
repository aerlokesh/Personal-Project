package com.aerloki.personal.project.Personal.Project.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.aerloki.personal.project.Personal.Project.model.CartItem;
import com.aerloki.personal.project.Personal.Project.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis-backed cart service for persistent cart storage
 * Allows users to maintain their cart across sessions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCartService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String CART_PREFIX = "cart:user:";
    private static final long CART_TTL_HOURS = 24;
    
    /**
     * Get cart items for a user
     * @param userId The user ID
     * @return List of cart items
     */
    public List<CartItem> getCartItems(Long userId) {
        try {
            String key = CART_PREFIX + userId;
            Object cartData = redisTemplate.opsForValue().get(key);
            
            if (cartData == null) {
                return new ArrayList<>();
            }
            
            String json = cartData.toString();
            return objectMapper.readValue(json, new TypeReference<List<CartItem>>() {});
        } catch (Exception e) {
            log.error("Error retrieving cart from Redis for user: {}", userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Save cart items for a user
     * @param userId The user ID
     * @param cartItems The cart items to save
     */
    public void saveCartItems(Long userId, List<CartItem> cartItems) {
        try {
            String key = CART_PREFIX + userId;
            String json = objectMapper.writeValueAsString(cartItems);
            redisTemplate.opsForValue().set(key, json, CART_TTL_HOURS, TimeUnit.HOURS);
            log.info("Saved cart to Redis for user: {} with {} items", userId, cartItems.size());
        } catch (JsonProcessingException e) {
            log.error("Error saving cart to Redis for user: {}", userId, e);
        }
    }
    
    /**
     * Add item to cart
     * @param userId The user ID
     * @param product The product to add
     * @param quantity The quantity to add
     */
    public void addToCart(Long userId, Product product, Integer quantity) {
        List<CartItem> cartItems = getCartItems(userId);
        
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            CartItem newItem = new CartItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                quantity,
                product.getImageUrl()
            );
            cartItems.add(newItem);
        }
        
        saveCartItems(userId, cartItems);
    }
    
    /**
     * Remove item from cart
     * @param userId The user ID
     * @param productId The product ID to remove
     */
    public void removeFromCart(Long userId, Long productId) {
        List<CartItem> cartItems = getCartItems(userId);
        cartItems.removeIf(item -> item.getProductId().equals(productId));
        saveCartItems(userId, cartItems);
    }
    
    /**
     * Update item quantity in cart
     * @param userId The user ID
     * @param productId The product ID
     * @param quantity The new quantity
     */
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        List<CartItem> cartItems = getCartItems(userId);
        
        if (quantity <= 0) {
            removeFromCart(userId, productId);
            return;
        }
        
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        
        saveCartItems(userId, cartItems);
    }
    
    /**
     * Clear cart for a user
     * @param userId The user ID
     */
    public void clearCart(Long userId) {
        try {
            String key = CART_PREFIX + userId;
            redisTemplate.delete(key);
            log.info("Cleared cart for user: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing cart from Redis for user: {}", userId, e);
        }
    }
    
    /**
     * Get cart total
     * @param userId The user ID
     * @return The total price
     */
    public BigDecimal getTotal(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);
        return cartItems.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get item count in cart
     * @param userId The user ID
     * @return The total number of items
     */
    public int getItemCount(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);
        return cartItems.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
    
    /**
     * Merge session cart with user's persistent cart
     * @param userId The user ID
     * @param sessionCart The cart items from the session
     */
    public void mergeCart(Long userId, List<CartItem> sessionCart) {
        if (sessionCart == null || sessionCart.isEmpty()) {
            return;
        }
        
        List<CartItem> persistentCart = getCartItems(userId);
        
        for (CartItem sessionItem : sessionCart) {
            boolean found = false;
            for (CartItem persistentItem : persistentCart) {
                if (persistentItem.getProductId().equals(sessionItem.getProductId())) {
                    persistentItem.setQuantity(persistentItem.getQuantity() + sessionItem.getQuantity());
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                persistentCart.add(sessionItem);
            }
        }
        
        saveCartItems(userId, persistentCart);
        log.info("Merged session cart with persistent cart for user: {}", userId);
    }
}
