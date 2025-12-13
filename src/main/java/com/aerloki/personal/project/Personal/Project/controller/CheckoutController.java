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
    private final CheckoutSession checkoutSession;
    
    public CheckoutController(CartService cartService, OrderService orderService, 
                            UserRepository userRepository, ProductRepository productRepository,
                            CheckoutSession checkoutSession) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.checkoutSession = checkoutSession;
    }
    
    // Step 1: Address
    @GetMapping
    public String showCheckout(Model model) {
        if (cartService.getCartItems().isEmpty()) {
            return "redirect:/cart";
        }
        checkoutSession.setCurrentStep(1);
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("session", checkoutSession);
        return "checkout-address";
    }
    
    @PostMapping("/address")
    public String processAddress(@RequestParam String fullName,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String addressLine1,
                                 @RequestParam(required = false) String addressLine2,
                                 @RequestParam String city,
                                 @RequestParam String state,
                                 @RequestParam String zipCode,
                                 @RequestParam(defaultValue = "United States") String country) {
        
        checkoutSession.setFullName(fullName);
        checkoutSession.setPhoneNumber(phoneNumber);
        checkoutSession.setAddressLine1(addressLine1);
        checkoutSession.setAddressLine2(addressLine2);
        checkoutSession.setCity(city);
        checkoutSession.setState(state);
        checkoutSession.setZipCode(zipCode);
        checkoutSession.setCountry(country);
        checkoutSession.setCurrentStep(2);
        
        logger.info("Address saved - Name: {}, City: {}, State: {}", fullName, city, state);
        
        return "redirect:/checkout/payment";
    }
    
    // Step 2: Payment
    @GetMapping("/payment")
    public String showPayment(Model model) {
        if (checkoutSession.getCurrentStep() < 2) {
            return "redirect:/checkout";
        }
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("session", checkoutSession);
        return "checkout-payment";
    }
    
    @PostMapping("/payment")
    public String processPayment(@RequestParam String paymentMethod,
                                 @RequestParam(required = false) String cardNumber,
                                 @RequestParam(required = false) String cardHolderName,
                                 @RequestParam(required = false) String expiryDate,
                                 @RequestParam(required = false) String cvv,
                                 @RequestParam(required = false) String upiId) {
        
        checkoutSession.setPaymentMethod(paymentMethod);
        
        if ("CREDIT_CARD".equals(paymentMethod) || "DEBIT_CARD".equals(paymentMethod)) {
            checkoutSession.setCardNumber(cardNumber);
            checkoutSession.setCardHolderName(cardHolderName);
            checkoutSession.setExpiryDate(expiryDate);
            checkoutSession.setCvv(cvv);
        } else if ("UPI".equals(paymentMethod)) {
            checkoutSession.setUpiId(upiId);
        }
        
        checkoutSession.setCurrentStep(3);
        
        logger.info("Payment method saved - Method: {}", paymentMethod);
        
        return "redirect:/checkout/review";
    }
    
    // Step 3: Review Order
    @GetMapping("/review")
    public String showReview(Model model) {
        if (checkoutSession.getCurrentStep() < 3) {
            return "redirect:/checkout";
        }
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("session", checkoutSession);
        return "checkout-review";
    }
    
    // Final step: Place Order
    @PostMapping("/place-order")
    public String placeOrder(Model model) {
        
        if (checkoutSession.getCurrentStep() < 3) {
            return "redirect:/checkout";
        }
        
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
        
        // Create order with checkout session data
        String fullAddress = checkoutSession.getFullAddress();
        Order order = orderService.createOrder(user, orderItems, fullAddress);
        
        logger.info("Order placed - ID: {}, Total: {}, Payment: {}, Address: {}", 
                    order.getId(), 
                    order.getTotalAmount(),
                    checkoutSession.getPaymentMethod(),
                    fullAddress);
        
        // Clear cart and reset checkout session
        cartService.clearCart();
        checkoutSession.reset();
        
        // Show confirmation
        model.addAttribute("order", order);
        model.addAttribute("session", checkoutSession);
        return "order-confirmation";
    }
}
