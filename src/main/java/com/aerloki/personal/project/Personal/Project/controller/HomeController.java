package com.aerloki.personal.project.Personal.Project.controller;

import com.aerloki.personal.project.Personal.Project.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    
    private final ProductService productService;
    
    public HomeController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/")
    public String home(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            // Use database search when search parameter is provided
            model.addAttribute("products", productService.searchProducts(search));
        } else {
            // Show all products when no search
            model.addAttribute("products", productService.getAvailableProducts());
        }
        return "index";
    }
}
