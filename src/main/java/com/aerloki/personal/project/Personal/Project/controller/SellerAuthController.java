package com.aerloki.personal.project.Personal.Project.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aerloki.personal.project.Personal.Project.model.Seller;
import com.aerloki.personal.project.Personal.Project.service.SellerService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Controller
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerAuthController {
    
    private final SellerService sellerService;
    
    @GetMapping("/signup")
    public String showSignupForm() {
        return "seller/signup";
    }
    
    @PostMapping("/signup")
    public String registerSeller(
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String businessName,
            @RequestParam(required = false, defaultValue = "") String password,
            @RequestParam(required = false, defaultValue = "") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("DEBUG - Seller Registration attempt:");
        System.out.println("  Email: [" + email + "]");
        System.out.println("  Business Name: [" + businessName + "]");
        
        if (email.isEmpty() || businessName.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "All fields are required");
            return "redirect:/seller/signup";
        }
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/seller/signup";
        }
        
        try {
            // Register the seller
            Seller seller = sellerService.registerSeller(email, businessName, password);
            
            // Auto-login after registration
            Authentication auth = new UsernamePasswordAuthenticationToken(
                email, 
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER"))
            );
            
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(auth);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            
            redirectAttributes.addFlashAttribute("successMessage", "Seller account created successfully!");
            return "redirect:/seller/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/seller/signup";
        }
    }
    
    @GetMapping("/signin")
    public String showSigninForm() {
        return "seller/signin";
    }
    
    @PostMapping("/signin")
    public String signin(
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (email.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email and password are required");
            return "redirect:/seller/signin";
        }
        
        try {
            // Find seller
            Seller seller = sellerService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
            
            // Validate password
            if (!sellerService.validatePassword(seller, password)) {
                throw new RuntimeException("Invalid email or password");
            }
            
            // Create authentication
            Authentication auth = new UsernamePasswordAuthenticationToken(
                email,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER"))
            );
            
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(auth);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            
            return "redirect:/seller/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/seller/signin";
        }
    }
}
