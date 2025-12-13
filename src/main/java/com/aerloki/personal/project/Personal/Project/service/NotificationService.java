package com.aerloki.personal.project.Personal.Project.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aerloki.personal.project.Personal.Project.model.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final JavaMailSender mailSender;
    
    /**
     * Send OTP email for signup verification
     */
    @Async
    public void sendOTPEmail(String toEmail, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@personalproject.com");
            message.setTo(toEmail);
            message.setSubject("Verify Your Email - OTP Code");
            message.setText(buildOTPEmailContent(otpCode));
            
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
    
    /**
     * Send order confirmation email
     */
    @Async
    public void sendOrderConfirmationEmail(String toEmail, Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@personalproject.com");
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - Order #" + order.getId());
            message.setText(buildOrderConfirmationContent(order));
            
            mailSender.send(message);
            log.info("Order confirmation email sent successfully to: {} for order: {}", toEmail, order.getId());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {} for order: {}", toEmail, order.getId(), e);
            // Don't throw exception to avoid breaking the order placement flow
        }
    }
    
    /**
     * Build OTP email content
     */
    private String buildOTPEmailContent(String otpCode) {
        return String.format("""
            Hello,
            
            Thank you for signing up with our e-commerce platform!
            
            Your One-Time Password (OTP) for email verification is:
            
            %s
            
            This OTP will expire in 10 minutes.
            
            If you didn't request this, please ignore this email.
            
            Best regards,
            Personal Project Team
            """, otpCode);
    }
    
    /**
     * Build order confirmation email content
     */
    private String buildOrderConfirmationContent(Order order) {
        StringBuilder content = new StringBuilder();
        content.append("Hello ").append(order.getUser().getName()).append(",\n\n");
        content.append("Thank you for your order!\n\n");
        content.append("Order Details:\n");
        content.append("Order ID: #").append(order.getId()).append("\n");
        content.append("Order Date: ").append(order.getOrderDate()).append("\n");
        content.append("Status: ").append(order.getStatus()).append("\n");
        content.append("Total Amount: $").append(order.getTotalAmount()).append("\n\n");
        
        content.append("Shipping Address:\n");
        content.append(order.getShippingAddress()).append("\n\n");
        
        content.append("Items Ordered:\n");
        order.getOrderItems().forEach(item -> {
            content.append("- ").append(item.getProduct().getName())
                   .append(" (Qty: ").append(item.getQuantity())
                   .append(", Price: $").append(item.getPrice())
                   .append(")\n");
        });
        
        content.append("\nWe'll send you another email when your order ships.\n\n");
        content.append("Thank you for shopping with us!\n\n");
        content.append("Best regards,\n");
        content.append("Personal Project Team");
        
        return content.toString();
    }
}
