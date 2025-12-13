package com.aerloki.personal.project.Personal.Project.controller;

import java.security.Principal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aerloki.personal.project.Personal.Project.service.OTPService;
import com.aerloki.personal.project.Personal.Project.service.UserService;
import com.aerloki.personal.project.Personal.Project.service.UserSessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final OTPService otpService;
    private final AuthenticationManager authenticationManager;
    
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
            HttpSession session,
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
            // Generate and send OTP to user's email
            otpService.generateAndSendOTP(email);
            
            // Store signup data in session for verification step
            session.setAttribute("signup_email", email);
            session.setAttribute("signup_name", name);
            session.setAttribute("signup_password", password);
            
            redirectAttributes.addFlashAttribute("success", "OTP sent to your email. Please verify to complete registration.");
            return "redirect:/verify-otp";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }
    
    @GetMapping("/verify-otp")
    public String showOTPVerificationForm(HttpSession session, Model model) {
        // Get signup data from session
        String email = (String) session.getAttribute("signup_email");
        String name = (String) session.getAttribute("signup_name");
        
        if (email == null) {
            return "redirect:/signup";
        }
        
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        
        return "verify-otp";
    }
    
    @PostMapping("/verify-otp")
    public String verifyOTP(
            @RequestParam(required = false, defaultValue = "") String otpCode,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        System.out.println("================================================================================");
        System.out.println("============ OTP VERIFICATION REQUEST RECEIVED ============");
        System.out.println("================================================================================");
        System.out.println("DEBUG - OTP Verification attempt:");
        System.out.println("  OTP Code: [" + otpCode + "]");
        
        // Get signup data from session
        String email = (String) session.getAttribute("signup_email");
        String name = (String) session.getAttribute("signup_name");
        String password = (String) session.getAttribute("signup_password");
        
        System.out.println("  Email from session: [" + email + "]");
        System.out.println("  Name from session: [" + name + "]");
        System.out.println("  Password from session: " + (password != null ? "present (length: " + password.length() + ")" : "null"));
        
        if (email == null || email.isEmpty()) {
            System.out.println("  ERROR: Session expired or email missing");
            redirectAttributes.addFlashAttribute("error", "Session expired. Please start registration again.");
            return "redirect:/signup";
        }
        
        if (otpCode.isEmpty()) {
            System.out.println("  ERROR: OTP code is empty");
            model.addAttribute("error", "OTP code is required");
            model.addAttribute("email", email);
            model.addAttribute("name", name);
            return "verify-otp";
        }
        
        try {
            System.out.println("  Verifying OTP...");
            // Verify OTP
            boolean verified = otpService.verifyOTP(email, otpCode);
            
            System.out.println("  OTP verification result: " + verified);
            
            if (!verified) {
                System.out.println("  ERROR: Invalid or expired OTP code");
                redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP code");
                return "redirect:/verify-otp";
            }
            
            System.out.println("  OTP verified successfully, registering user...");
            // OTP verified, now register the user
            userService.registerUser(email, name, password);
            
            System.out.println("  User registered successfully, authenticating user...");
            
            // Automatically authenticate the user after successful registration
            try {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, password);
                Authentication authentication = authenticationManager.authenticate(authToken);
                
                // Set the authentication in the security context
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authentication);
                
                // Store security context in session
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
                
                System.out.println("  User authenticated successfully");
                
                // Save last login email
                userSessionService.saveLastLoginEmail(email);
                
                System.out.println("  Clearing signup session data...");
                // Clear session data after successful registration
                session.removeAttribute("signup_email");
                session.removeAttribute("signup_name");
                session.removeAttribute("signup_password");
                
                System.out.println("  Redirecting to home page - user is now logged in");
                redirectAttributes.addFlashAttribute("success", "Registration successful! Welcome to our store.");
                return "redirect:/";
            } catch (Exception authException) {
                System.out.println("  ERROR during authentication: " + authException.getMessage());
                authException.printStackTrace();
                // If authentication fails, still clear session and redirect to signin
                session.removeAttribute("signup_email");
                session.removeAttribute("signup_name");
                session.removeAttribute("signup_password");
                redirectAttributes.addFlashAttribute("success", "Registration successful! Please sign in.");
                return "redirect:/signin";
            }
        } catch (RuntimeException e) {
            System.out.println("  ERROR during registration: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/verify-otp";
        }
    }
    
    @GetMapping("/signin")
    public String showSigninForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        
        // Pre-fill the last login email for better user experience
        String lastEmail = userSessionService.getLastLoginEmail();
        if (lastEmail != null) {
            model.addAttribute("lastEmail", lastEmail);
        }
        
        return "signin";
    }
    
    /**
     * Post-login handler to save the email for next login
     */
    @GetMapping("/login-success")
    public String handleLoginSuccess(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            userSessionService.saveLastLoginEmail(email);
        }
        return "redirect:/";
    }
}
