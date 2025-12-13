package com.aerloki.personal.project.Personal.Project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerloki.personal.project.Personal.Project.model.Order;
import com.aerloki.personal.project.Personal.Project.model.OrderItem;
import com.aerloki.personal.project.Personal.Project.model.User;
import com.aerloki.personal.project.Personal.Project.repository.OrderRepository;
import com.aerloki.personal.project.Personal.Project.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }
    
    @Cacheable(value = "orders", key = "'all'")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Cacheable(value = "orders", key = "'id_' + #id")
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    @Cacheable(value = "orders", key = "'user_' + #userId")
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    @CacheEvict(value = "orders", allEntries = true)
    public Order createOrder(User user, List<OrderItem> items, String shippingAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            item.setOrder(order);
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        
        order.setOrderItems(items);
        order.setTotalAmount(total);
        
        Order savedOrder = orderRepository.save(order);
        
        // Send order confirmation email asynchronously
        try {
            notificationService.sendOrderConfirmationEmail(user.getEmail(), savedOrder);
            log.info("Order confirmation email queued for order: {}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Failed to queue order confirmation email for order: {}", savedOrder.getId(), e);
            // Don't throw exception - order is already created
        }
        
        return savedOrder;
    }
    
    @CacheEvict(value = "orders", allEntries = true)
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
