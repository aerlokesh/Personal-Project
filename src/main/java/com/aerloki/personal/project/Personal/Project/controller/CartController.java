package com.aerloki.personal.project.Personal.Project.controller;

import com.aerloki.personal.project.Personal.Project.model.Product;
import com.aerloki.personal.project.Personal.Project.service.CartService;
import com.aerloki.personal.project.Personal.Project.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    private final CartService cartService;
    private final ProductService productService;
    
    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }
    
    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
        return "cart";
    }
    
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, 
                           @RequestParam(defaultValue = "1") Integer quantity) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        cartService.addToCart(product, quantity);
        return "redirect:/cart";
    }
    
    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }
    
    @PostMapping("/update/{id}")
    public String updateQuantity(@PathVariable Long id, 
                                @RequestParam Integer quantity) {
        cartService.updateQuantity(id, quantity);
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}
