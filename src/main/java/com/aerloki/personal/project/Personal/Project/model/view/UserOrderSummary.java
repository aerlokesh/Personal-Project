package com.aerloki.personal.project.Personal.Project.model.view;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Materialized View Entity for User Order Summary
 * Read-only entity mapped to mv_user_order_summary
 */
@Entity
@Immutable
@Table(name = "mv_user_order_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderSummary {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "total_orders")
    private Long totalOrders;
    
    @Column(name = "total_spent")
    private BigDecimal totalSpent;
    
    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;
    
    @Column(name = "pending_orders")
    private Long pendingOrders;
    
    @Column(name = "confirmed_orders")
    private Long confirmedOrders;
    
    @Column(name = "shipped_orders")
    private Long shippedOrders;
    
    @Column(name = "delivered_orders")
    private Long deliveredOrders;
}
