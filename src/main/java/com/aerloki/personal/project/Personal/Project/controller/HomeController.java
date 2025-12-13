package com.aerloki.personal.project.Personal.Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aerloki.personal.project.Personal.Project.service.CartService;
import com.aerloki.personal.project.Personal.Project.service.ProductService;

@Controller
public class HomeController {
    
    private final ProductService productService;
    private final CartService cartService;
    
    public HomeController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }
    
    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Model model) {
        
        // Add cart item count to model
        model.addAttribute("cartItemCount", cartService.getItemCount());
        
        if (category != null && !category.trim().isEmpty()) {
            // Filter by category
            model.addAttribute("products", productService.getProductsByCategory(category));
        } else if (search != null && !search.trim().isEmpty()) {
            // Use database search when search parameter is provided
            model.addAttribute("products", productService.searchProducts(search));
        } else {
            // Show all products when no search or category
            model.addAttribute("products", productService.getAvailableProducts());
        }
        return "index";
    }
}
