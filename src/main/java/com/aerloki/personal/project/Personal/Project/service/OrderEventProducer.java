package com.aerloki.personal.project.Personal.Project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.aerloki.personal.project.Personal.Project.event.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {
    
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    
    @Value("${kafka.topic.order-placed}")
    private String orderPlacedTopic;
    
    /**
     * Publishes an order placed event to Kafka
     * 
     * @param event The order placed event to publish
     */
    public void publishOrderPlacedEvent(OrderPlacedEvent event) {
        try {
            log.info("Publishing order placed event for order ID: {}", event.getOrderId());
            
            kafkaTemplate.send(orderPlacedTopic, event.getOrderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published order event for order ID: {} to topic: {}", 
                            event.getOrderId(), orderPlacedTopic);
                    } else {
                        log.error("Failed to publish order event for order ID: {}", 
                            event.getOrderId(), ex);
                    }
                });
        } catch (Exception e) {
            log.error("Error publishing order placed event for order ID: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to publish order event", e);
        }
    }
}
