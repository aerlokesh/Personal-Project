package com.aerloki.personal.project.Personal.Project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aerloki.personal.project.Personal.Project.dto.ProductForm;
import com.aerloki.personal.project.Personal.Project.model.Product;
import com.aerloki.personal.project.Personal.Project.model.Seller;
import com.aerloki.personal.project.Personal.Project.service.ProductService;
import com.aerloki.personal.project.Personal.Project.service.S3Service;
import com.aerloki.personal.project.Personal.Project.service.SellerService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {
    
    private final ProductService productService;
    private final SellerService sellerService;
    private final S3Service s3Service;
    
    // Seller Dashboard - list all products
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Verify user is a seller
        if (!isUserSeller(authentication)) {
            return "redirect:/";
        }
        
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("availableProducts", products.stream().filter(Product::getAvailable).count());
        model.addAttribute("outOfStock", products.stream().filter(p -> p.getStock() == 0).count());
        
        return "seller/dashboard";
    }
    
    // Show add product form
    @GetMapping("/products/add")
    public String showAddProductForm(Model model, Authentication authentication) {
        if (!isUserSeller(authentication)) {
            return "redirect:/";
        }
        
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("isEdit", false);
        return "seller/product-form";
    }
    
    // Handle add product
    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute ProductForm productForm,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (!isUserSeller(authentication)) {
            return "redirect:/";
        }
        
        try {
            Product product = new Product();
            product.setAsin(productForm.getAsin());
            product.setName(productForm.getName());
            product.setDescription(productForm.getDescription());
            product.setPrice(productForm.getPrice());
            product.setStock(productForm.getStock());
            product.setCategory(productForm.getCategory());
            product.setAvailable(productForm.getAvailable());
            
            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
                String imageUrl = s3Service.uploadImage(fileName, imageFile.getBytes(), imageFile.getContentType());
                product.setImageUrl(imageUrl);
            } else if (productForm.getImageUrl() != null && !productForm.getImageUrl().isEmpty()) {
                product.setImageUrl(productForm.getImageUrl());
            }
            
            productService.saveProduct(product);
            
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
            return "redirect:/seller/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding product: " + e.getMessage());
            return "redirect:/seller/products/add";
        }
    }
    
    // Show edit product form
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, 
                                     Model model, 
                                     Authentication authentication) {
        if (!isUserSeller(authentication)) {
            return "redirect:/";
        }
        
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        ProductForm productForm = new ProductForm();
        productForm.setAsin(product.getAsin());
        productForm.setName(product.getName());
        productForm.setDescription(product.getDescription());
        productForm.setPrice(product.getPrice());
        productForm.setStock(product.getStock());
        productForm.setImageUrl(product.getImageUrl());
        productForm.setCategory(product.getCategory());
        productForm.setAvailable(product.getAvailable());
        
        model.addAttribute("productForm", productForm);
        model.addAttribute("productId", id);
        model.addAttribute("isEdit", true);
        
        return "seller/product-form";
    }
    
    // Handle edit product
    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id,
                            @ModelAttribute ProductForm productForm,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (!isUserSeller(authentication)) {
            return "redirect:/";
        }
        
        try {
            Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            product.setAsin(productForm.getAsin());
            product.setName(productForm.getName());
            product.setDescription(productForm.getDescription());
            product.setPrice(productForm.getPrice());
            product.setStock(productForm.getStock());
            product.setCategory(productForm.getCategory());
            product.setAvailable(productForm.getAvailable());
            
            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
                String imageUrl = s3Service.uploadImage(fileName, imageFile.getBytes(), imageFile.getContentType());
                product.setImageUrl(imageUrl);
            } else if (productForm.getImageUrl() != null && !productForm.getImageUrl().isEmpty()) {
                product.setImageUrl(productForm.getImageUrl());
            }
            // If neither file nor URL provided, keep existing image
            
            productService.saveProduct(product);
            
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            return "redirect:/seller/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/seller/products/edit/" + id;
        }
    }
    
    // Handle delete product
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (!isUserSeller(authentication)) {
            return "redirect:/";
        }
        
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        
        return "redirect:/seller/dashboard";
    }
    
    // Toggle product availability
    @PostMapping("/products/toggle-availability/{id}")
    public String toggleAvailability(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        if (!isUserSeller(authentication)) {
            return "redirect:/";
        }
        
        try {
            Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            product.setAvailable(!product.getAvailable());
            productService.saveProduct(product);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Product " + (product.getAvailable() ? "enabled" : "disabled") + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
        }
        
        return "redirect:/seller/dashboard";
    }
    
    // Helper method to check if user is a seller
    private boolean isUserSeller(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String email = authentication.getName();
        // Check if email belongs to a seller
        return sellerService.findByEmail(email).isPresent();
    }
}
