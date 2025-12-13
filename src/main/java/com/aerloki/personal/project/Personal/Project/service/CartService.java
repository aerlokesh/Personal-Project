package com.aerloki.personal.project.Personal.Project.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.aerloki.personal.project.Personal.Project.model.CartItem;
import com.aerloki.personal.project.Personal.Project.model.Product;

@Service
@SessionScope
public class CartService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<CartItem> cartItems = new ArrayList<>();
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public void addToCart(Product product, Integer quantity) {
        Optional<CartItem> existingItem = cartItems.stream()
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
            cartItems.add(newItem);
        }
    }
    
    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProductId().equals(productId));
    }
    
    public void updateQuantity(Long productId, Integer quantity) {
        cartItems.stream()
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
    
    public void clearCart() {
        cartItems.clear();
    }
    
    public BigDecimal getTotal() {
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getItemCount() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
