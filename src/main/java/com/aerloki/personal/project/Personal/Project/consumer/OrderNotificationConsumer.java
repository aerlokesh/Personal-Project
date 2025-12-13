package com.aerloki.personal.project.Personal.Project.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.aerloki.personal.project.Personal.Project.event.OrderPlacedEvent;
import com.aerloki.personal.project.Personal.Project.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationConsumer {
    
    private final NotificationService notificationService;
    
    /**
     * Consumes order placed events and sends notifications to customers
     * This runs asynchronously after the order is placed
     * 
     * @param event The order placed event
     */
    @KafkaListener(
        topics = "${kafka.topic.order-placed}",
        groupId = "notification-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void sendOrderNotification(OrderPlacedEvent event) {
        try {
            log.info("Processing order notification for order ID: {}", event.getOrderId());
            
            // Build email content
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Dear ").append(event.getUserName()).append(",\n\n");
            emailContent.append("Thank you for your order! Your order has been successfully placed.\n\n");
            emailContent.append("Order Details:\n");
            emailContent.append("Order ID: ").append(event.getOrderId()).append("\n");
            emailContent.append("Order Date: ").append(event.getOrderDate()).append("\n");
            emailContent.append("Total Amount: $").append(event.getTotalAmount()).append("\n\n");
            
            emailContent.append("Items Ordered:\n");
            for (OrderPlacedEvent.OrderItemDetail item : event.getItems()) {
                emailContent.append("- ").append(item.getProductName())
                    .append(" (").append(item.getProductAsin()).append(")")
                    .append(" x ").append(item.getQuantity())
                    .append(" - $").append(item.getSubtotal()).append("\n");
            }
            
            emailContent.append("\nShipping Address:\n");
            emailContent.append(event.getShippingAddress()).append("\n\n");
            
            emailContent.append("Payment Method: ").append(event.getPaymentMethod()).append("\n\n");
            
            emailContent.append("Your order will be processed and shipped soon.\n");
            emailContent.append("Thank you for shopping with us!\n\n");
            emailContent.append("Best regards,\n");
            emailContent.append("E-Commerce Team");
            
            // Send email
            notificationService.sendEmail(
                event.getUserEmail(),
                "Order Confirmation - Order #" + event.getOrderId(),
                emailContent.toString()
            );
            
            log.info("Successfully sent order notification for order ID: {}", event.getOrderId());
            
        } catch (Exception e) {
            log.error("Error sending order notification for order ID: {}", event.getOrderId(), e);
            // Don't throw exception - notification failures shouldn't affect order processing
        }
    }
}
