package com.aerloki.personal.project.Personal.Project.controller;

import com.aerloki.personal.project.Personal.Project.model.*;
import com.aerloki.personal.project.Personal.Project.repository.ProductRepository;
import com.aerloki.personal.project.Personal.Project.repository.UserRepository;
import com.aerloki.personal.project.Personal.Project.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    
    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    public CheckoutController(CartService cartService, OrderService orderService, 
                            UserRepository userRepository, ProductRepository productRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }
    
    @GetMapping
    public String showCheckout(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        return "checkout";
    }
    
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam(name = "name", required = false) String name,
                            @RequestParam(name = "address", required = false) String address,
                            Model model) {
        
        // Structured logging for ELK stack
        logger.info("Order placement initiated - Name: {}, Address: {}", 
                    name != null ? name : "NOT_PROVIDED", 
                    address != null ? address : "NOT_PROVIDED");
        
        // Get cart items
        List<CartItem> cartItems = cartService.getCartItems();
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        // Use first test user for demo
        User user = userRepository.findById(1L).orElseThrow();
        
        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItems.add(orderItem);
        }
        
        Order order = orderService.createOrder(user, orderItems, address);
        
        logger.info("Order created successfully - Order ID: {}, Total: {}, Address: {}", 
                    order.getId(), 
                    order.getTotalAmount(), 
                    order.getShippingAddress() != null ? order.getShippingAddress() : "EMPTY");
        
        // Clear cart
        cartService.clearCart();
        
        // Show confirmation
        model.addAttribute("order", order);
        return "order-confirmation";
    }
}
