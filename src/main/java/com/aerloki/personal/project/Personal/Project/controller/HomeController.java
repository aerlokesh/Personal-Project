package com.aerloki.personal.project.Personal.Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aerloki.personal.project.Personal.Project.service.CartService;
import com.aerloki.personal.project.Personal.Project.service.ProductService;
import com.aerloki.personal.project.Personal.Project.service.MaterializedViewService;

@Controller
public class HomeController {
    
    private final ProductService productService;
    private final CartService cartService;
    private final MaterializedViewService viewService;
    
    public HomeController(ProductService productService, CartService cartService, MaterializedViewService viewService) {
        this.productService = productService;
        this.cartService = cartService;
        this.viewService = viewService;
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
            // Add popular products for this category from materialized view
            model.addAttribute("popularProducts", viewService.getPopularProductsByCategory(category, 5));
        } else if (search != null && !search.trim().isEmpty()) {
            // Use database search when search parameter is provided
            model.addAttribute("products", productService.searchProducts(search));
        } else {
            // Show all products when no search or category
            model.addAttribute("products", productService.getAvailableProducts());
            // Add top popular products from materialized view (much faster than querying orders)
            model.addAttribute("popularProducts", viewService.getPopularProducts(5));
        }
        
        // Add category statistics from materialized view
        model.addAttribute("categoryStats", viewService.getAllCategoryStatistics());
        
        return "index";
    }
}
