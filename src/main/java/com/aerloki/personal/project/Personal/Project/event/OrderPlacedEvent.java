package com.aerloki.personal.project.Personal.Project.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent implements Serializable {
    
    private Long orderId;
    private String userEmail;
    private String userName;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemDetail> items;
    private String shippingAddress;
    private String paymentMethod;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDetail implements Serializable {
        private Long productId;
        private String productName;
        private String productAsin;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }
}
