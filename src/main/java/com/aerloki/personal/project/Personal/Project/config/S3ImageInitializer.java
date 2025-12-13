package com.aerloki.personal.project.Personal.Project.config;

import com.aerloki.personal.project.Personal.Project.service.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.nio.charset.StandardCharsets;

@Configuration
public class S3ImageInitializer {
    
    @Bean
    @Order(1)  // Run before DataInitializer
    CommandLineRunner initS3Images(S3Service s3Service) {
        return args -> {
            System.out.println("Uploading product images to S3...");
            
            // Create and upload 35 different colored placeholder images
            String[] colors = {
                "#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", "#98D8C8",
                "#F7DC6F", "#BB8FCE", "#85C1E2", "#F8B739", "#52B788",
                "#E76F51", "#2A9D8F", "#E9C46A", "#F4A261", "#264653",
                "#8E44AD", "#3498DB", "#1ABC9C", "#F39C12", "#E74C3C",
                "#95A5A6", "#34495E", "#16A085", "#27AE60", "#2980B9",
                "#8E44AD", "#2C3E50", "#F1C40F", "#E67E22", "#ECF0F1",
                "#BDC3C7", "#7F8C8D", "#D35400", "#C0392B", "#9B59B6"
            };
            
            for (int i = 1; i <= 35; i++) {
                String color = colors[i - 1];
                String svgContent = generateSvgImage(i, color);
                byte[] imageBytes = svgContent.getBytes(StandardCharsets.UTF_8);
                
                String url = s3Service.uploadImage("product-" + i + ".svg", imageBytes, "image/svg+xml");
                if (url != null && i == 1) {
                    System.out.println("✓ Sample S3 image URL: " + url);
                }
            }
            
            System.out.println("✓ Uploaded 35 product images to S3");
        };
    }
    
    private String generateSvgImage(int productNumber, String color) {
        return String.format(
            "<svg width=\"400\" height=\"400\" xmlns=\"http://www.w3.org/2000/svg\">" +
            "<rect width=\"400\" height=\"400\" fill=\"%s\"/>" +
            "<text x=\"50%%\" y=\"45%%\" font-family=\"Arial, sans-serif\" font-size=\"48\" " +
            "fill=\"white\" text-anchor=\"middle\" font-weight=\"bold\">Product</text>" +
            "<text x=\"50%%\" y=\"60%%\" font-family=\"Arial, sans-serif\" font-size=\"72\" " +
            "fill=\"white\" text-anchor=\"middle\" font-weight=\"bold\">%d</text>" +
            "</svg>",
            color, productNumber
        );
    }
}
