package com.aerloki.personal.project.Personal.Project.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.aerloki.personal.project.Personal.Project.model.CartItem;
import com.aerloki.personal.project.Personal.Project.model.Product;
import com.aerloki.personal.project.Personal.Project.model.User;

@Service
@SessionScope
public class CartService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final RedisCartService redisCartService;
    private final UserService userService;
    private List<CartItem> sessionCart = new ArrayList<>();
    
    public CartService(RedisCartService redisCartService, UserService userService) {
        this.redisCartService = redisCartService;
        this.userService = userService;
    }
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userService.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }
    
    public List<CartItem> getCartItems() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return redisCartService.getCartItems(userId);
        }
        return sessionCart;
    }
    
    public void addToCart(Product product, Integer quantity) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            redisCartService.addToCart(userId, product, quantity);
        } else {
            Optional<CartItem> existingItem = sessionCart.stream()
                    .filter(item -> item.getProductId().equals(product.getId()))
                    .findFirst();
            
            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + quantity);
            } else {
                CartItem newItem = new CartItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        quantity,
                        product.getImageUrl()
                );
                sessionCart.add(newItem);
            }
        }
    }
    
    public void removeFromCart(Long productId) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            redisCartService.removeFromCart(userId, productId);
        } else {
            sessionCart.removeIf(item -> item.getProductId().equals(productId));
        }
    }
    
    public void updateQuantity(Long productId, Integer quantity) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            redisCartService.updateQuantity(userId, productId, quantity);
        } else {
            sessionCart.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(item -> {
                        if (quantity <= 0) {
                            removeFromCart(productId);
                        } else {
                            item.setQuantity(quantity);
                        }
                    });
        }
    }
    
    public void clearCart() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            redisCartService.clearCart(userId);
        } else {
            sessionCart.clear();
        }
    }
    
    public BigDecimal getTotal() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return redisCartService.getTotal(userId);
        }
        return sessionCart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getItemCount() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return redisCartService.getItemCount(userId);
        }
        return sessionCart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public void mergeWithRedis() {
        Long userId = getCurrentUserId();
        if (userId != null && !sessionCart.isEmpty()) {
            redisCartService.mergeCart(userId, sessionCart);
            sessionCart.clear();
        }
    }
}
