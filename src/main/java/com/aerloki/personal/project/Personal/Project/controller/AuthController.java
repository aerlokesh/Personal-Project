package com.aerloki.personal.project.Personal.Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aerloki.personal.project.Personal.Project.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }
    
    @PostMapping("/signup")
    public String registerUser(
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String password,
            @RequestParam(required = false, defaultValue = "") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("DEBUG - Registration attempt:");
        System.out.println("  Email: [" + email + "]");
        System.out.println("  Name: [" + name + "]");
        System.out.println("  Password length: " + password.length());
        
        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "All fields are required");
            return "redirect:/signup";
        }
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/signup";
        }
        
        try {
            userService.registerUser(email, name, password);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please sign in.");
            return "redirect:/signin";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }
    
    @GetMapping("/signin")
    public String showSigninForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        return "signin";
    }
}
