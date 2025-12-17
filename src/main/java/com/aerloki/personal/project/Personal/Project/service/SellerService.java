package com.aerloki.personal.project.Personal.Project.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerloki.personal.project.Personal.Project.model.Seller;
import com.aerloki.personal.project.Personal.Project.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {
    
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public Seller registerSeller(String email, String businessName, String password) {
        System.out.println("DEBUG - Attempting to register seller:");
        System.out.println("  Email: [" + email + "]");
        System.out.println("  Business Name: [" + businessName + "]");
        
        if (sellerRepository.findByEmail(email).isPresent()) {
            System.out.println("  ERROR: Seller already exists with email: " + email);
            throw new RuntimeException("Seller already exists with email: " + email);
        }
        
        Seller seller = new Seller();
        seller.setEmail(email);
        seller.setBusinessName(businessName);
        seller.setPassword(passwordEncoder.encode(password));
        
        System.out.println("  Saving seller to database...");
        
        Seller savedSeller = sellerRepository.save(seller);
        
        System.out.println("  Seller saved successfully with ID: " + savedSeller.getId());
        return savedSeller;
    }
    
    public Optional<Seller> findByEmail(String email) {
        return sellerRepository.findByEmail(email);
    }
    
    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
    }
    
    public boolean validatePassword(Seller seller, String rawPassword) {
        return passwordEncoder.matches(rawPassword, seller.getPassword());
    }
}
