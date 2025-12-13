package com.aerloki.personal.project.Personal.Project.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerloki.personal.project.Personal.Project.event.OrderPlacedEvent;
import com.aerloki.personal.project.Personal.Project.model.Product;
import com.aerloki.personal.project.Personal.Project.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryUpdateConsumer {
    
    private final ProductRepository productRepository;
    
    /**
     * Consumes order placed events and updates inventory
     * This runs asynchronously after the order is placed
     * 
     * @param event The order placed event
     */
    @KafkaListener(
        topics = "${kafka.topic.order-placed}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void updateInventory(OrderPlacedEvent event) {
        try {
            log.info("Processing inventory update for order ID: {}", event.getOrderId());
            
            for (OrderPlacedEvent.OrderItemDetail item : event.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                        "Product not found: " + item.getProductId()));
                
                int currentStock = product.getStock();
                int newStock = currentStock - item.getQuantity();
                
                log.info("Updating inventory for product {}: {} -> {} (ordered: {})", 
                    product.getName(), currentStock, newStock, item.getQuantity());
                
                if (newStock < 0) {
                    log.error("Negative stock detected for product {}: {}", 
                        product.getName(), newStock);
                    // This shouldn't happen due to distributed locks, but log it
                }
                
                product.setStock(newStock);
                
                // Mark as unavailable if out of stock
                if (newStock <= 0) {
                    product.setAvailable(false);
                    log.warn("Product {} is now out of stock and marked unavailable", 
                        product.getName());
                }
                
                productRepository.save(product);
            }
            
            log.info("Successfully updated inventory for order ID: {}", event.getOrderId());
            
        } catch (Exception e) {
            log.error("Error updating inventory for order ID: {}", event.getOrderId(), e);
            // In production, you might want to publish to a dead letter queue
            throw e;
        }
    }
}
