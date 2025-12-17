package com.aerloki.personal.project.Personal.Project.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aerloki.personal.project.Personal.Project.model.Order;
import com.aerloki.personal.project.Personal.Project.model.User;
import com.aerloki.personal.project.Personal.Project.repository.OrderRepository;
import com.aerloki.personal.project.Personal.Project.repository.UserRepository;
import com.aerloki.personal.project.Personal.Project.service.MaterializedViewService;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MaterializedViewService viewService;
    
    public OrderController(OrderRepository orderRepository, UserRepository userRepository, MaterializedViewService viewService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.viewService = viewService;
    }
    
    @GetMapping
    public String viewOrders(Model model, Authentication authentication) {
        // Get currently logged-in user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Order> orders = orderRepository.findByUserId(user.getId());
        model.addAttribute("orders", orders);
        
        // Add user order summary from materialized view (much faster than aggregating orders)
        viewService.getUserOrderSummary(user.getId())
                .ifPresent(summary -> model.addAttribute("orderSummary", summary));
        
        return "orders";
    }
    
    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "order-details";
    }
}
