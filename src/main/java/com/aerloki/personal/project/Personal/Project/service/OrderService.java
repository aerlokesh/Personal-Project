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

@Service
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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
        
        return orderRepository.save(order);
    }
    
    @CacheEvict(value = "orders", allEntries = true)
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
