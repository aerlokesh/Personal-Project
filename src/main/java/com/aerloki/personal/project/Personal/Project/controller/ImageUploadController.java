package com.aerloki.personal.project.Personal.Project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.aerloki.personal.project.Personal.Project.service.S3Service;
import com.aerloki.personal.project.Personal.Project.service.SellerService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class ImageUploadController {
    
    private final S3Service s3Service;
    private final SellerService sellerService;
    
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("image") MultipartFile imageFile,
            Authentication authentication) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Verify user is a seller
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("error", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            String email = authentication.getName();
            if (sellerService.findByEmail(email).isEmpty()) {
                response.put("error", "Not authorized");
                return ResponseEntity.status(403).body(response);
            }
            
            // Validate file
            if (imageFile.isEmpty()) {
                response.put("error", "No file selected");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload to S3
            String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
            String imageUrl = s3Service.uploadImage(fileName, imageFile.getBytes(), imageFile.getContentType());
            
            if (imageUrl == null) {
                response.put("error", "Failed to upload image");
                return ResponseEntity.status(500).body(response);
            }
            
            response.put("success", "true");
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
