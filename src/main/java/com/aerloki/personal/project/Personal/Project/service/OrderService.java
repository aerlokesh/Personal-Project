package com.aerloki.personal.project.Personal.Project.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerloki.personal.project.Personal.Project.event.OrderPlacedEvent;
import com.aerloki.personal.project.Personal.Project.model.Order;
import com.aerloki.personal.project.Personal.Project.model.OrderItem;
import com.aerloki.personal.project.Personal.Project.model.Product;
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
    private final DistributedLockService distributedLockService;
    private final OrderEventProducer orderEventProducer;
    
    public OrderService(
            OrderRepository orderRepository, 
            ProductRepository productRepository, 
            NotificationService notificationService,
            DistributedLockService distributedLockService,
            OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.distributedLockService = distributedLockService;
        this.orderEventProducer = orderEventProducer;
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
        log.info("Creating order for user: {} with {} items", user.getEmail(), items.size());
        
        // Extract product IDs for locking
        Long[] productIds = items.stream()
            .map(item -> item.getProduct().getId())
            .toArray(Long[]::new);
        
        // Use distributed locks to prevent race conditions
        final Order[] orderHolder = new Order[1];
        final RuntimeException[] exceptionHolder = new RuntimeException[1];
        
        boolean lockAcquired = distributedLockService.executeWithMultipleLocks(productIds, () -> {
            try {
                // Check stock availability for all items within the lock
                for (OrderItem item : items) {
                    Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException(
                            "Product not found: " + item.getProduct().getId()));
                    
                    if (product.getStock() < item.getQuantity()) {
                        throw new RuntimeException(
                            "Insufficient stock for product: " + product.getName() + 
                            ". Available: " + product.getStock() + 
                            ", Requested: " + item.getQuantity());
                    }
                    
                    if (!product.getAvailable()) {
                        throw new RuntimeException(
                            "Product is no longer available: " + product.getName());
                    }
                }
                
                log.info("Stock validation passed for all items");
                
                // Create the order
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
                orderHolder[0] = savedOrder;
                
                log.info("Order created successfully with ID: {}", savedOrder.getId());
                
                // Immediately update stock to prevent overselling
                // (This happens within the lock before publishing event)
                for (OrderItem item : items) {
                    Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException(
                            "Product not found: " + item.getProduct().getId()));
                    
                    int newStock = product.getStock() - item.getQuantity();
                    product.setStock(newStock);
                    
                    if (newStock <= 0) {
                        product.setAvailable(false);
                    }
                    
                    productRepository.save(product);
                    log.info("Updated stock for product {}: {}", 
                        product.getName(), newStock);
                }
                
            } catch (RuntimeException e) {
                exceptionHolder[0] = e;
                throw e;
            }
        });
        
        if (!lockAcquired) {
            log.error("Could not acquire distributed lock for products: {}", (Object) productIds);
            throw new RuntimeException(
                "Unable to process order at this time. Please try again.");
        }
        
        if (exceptionHolder[0] != null) {
            throw exceptionHolder[0];
        }
        
        Order savedOrder = orderHolder[0];
        
        // Publish Kafka event for async processing (notifications, analytics, etc.)
        try {
            OrderPlacedEvent event = buildOrderPlacedEvent(savedOrder);
            orderEventProducer.publishOrderPlacedEvent(event);
            log.info("Order placed event published for order ID: {}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Failed to publish order event for order ID: {}", savedOrder.getId(), e);
            // Don't throw exception - order is already created and stock updated
        }
        
        return savedOrder;
    }
    
    /**
     * Build OrderPlacedEvent from Order
     */
    private OrderPlacedEvent buildOrderPlacedEvent(Order order) {
        List<OrderPlacedEvent.OrderItemDetail> itemDetails = order.getOrderItems().stream()
            .map(item -> new OrderPlacedEvent.OrderItemDetail(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getAsin(),
                item.getQuantity(),
                item.getPrice(),
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            ))
            .toList();
        
        return new OrderPlacedEvent(
            order.getId(),
            order.getUser().getEmail(),
            order.getUser().getName(),
            order.getTotalAmount(),
            order.getOrderDate(),
            itemDetails,
            order.getShippingAddress(),
            "Credit Card" // Default for now, can be extended
        );
    }
    
    @CacheEvict(value = "orders", allEntries = true)
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
