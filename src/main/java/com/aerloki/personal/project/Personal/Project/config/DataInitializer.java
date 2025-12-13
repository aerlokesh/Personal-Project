package com.aerloki.personal.project.Personal.Project.config;

import com.aerloki.personal.project.Personal.Project.model.Product;
import com.aerloki.personal.project.Personal.Project.model.User;
import com.aerloki.personal.project.Personal.Project.repository.ProductRepository;
import com.aerloki.personal.project.Personal.Project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository, UserRepository userRepository) {
        return args -> {
            // Create fake users
            if (userRepository.count() == 0) {
                userRepository.save(new User(null, "john.doe@example.com", "John Doe", "password123", 
                        "123 Main St, Seattle, WA", "206-555-0100"));
                userRepository.save(new User(null, "jane.smith@example.com", "Jane Smith", "password123", 
                        "456 Oak Ave, Portland, OR", "503-555-0200"));
                userRepository.save(new User(null, "bob.jones@example.com", "Bob Jones", "password123", 
                        "789 Pine Rd, San Francisco, CA", "415-555-0300"));
                System.out.println("✓ Created 3 test users");
            }
            
            // Create fake products with ASINs
            if (productRepository.count() == 0) {
                // Electronics
                productRepository.save(new Product(null, "B08N5WRWNW", "Echo Dot (4th Gen)", 
                        "Smart speaker with Alexa - Charcoal", 
                        new BigDecimal("49.99"), 150, 
                        "https://m.media-amazon.com/images/I/61EXU8BuGWL._AC_SL1000_.jpg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B0BXVHPL5M", "Fire TV Stick 4K Max", 
                        "Streaming device with Alexa Voice Remote", 
                        new BigDecimal("59.99"), 200, 
                        "https://m.media-amazon.com/images/I/51TjJOTfslL._AC_SL1000_.jpg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B09B8RXYM5", "Kindle Paperwhite (11th Gen)", 
                        "Now with a 6.8\" display and adjustable warm light", 
                        new BigDecimal("139.99"), 100, 
                        "https://m.media-amazon.com/images/I/61W7iKiT0uL._AC_SL1000_.jpg", 
                        "Electronics", true));
                
                // Books
                productRepository.save(new Product(null, "B0C3JYL5ZM", "Atomic Habits", 
                        "An Easy & Proven Way to Build Good Habits & Break Bad Ones", 
                        new BigDecimal("16.99"), 500, 
                        "https://m.media-amazon.com/images/I/81YkqyaFVEL._AC_UL320_.jpg", 
                        "Books", true));
                
                productRepository.save(new Product(null, "B07XJ8C8F7", "The Midnight Library", 
                        "A Novel by Matt Haig", 
                        new BigDecimal("14.99"), 350, 
                        "https://m.media-amazon.com/images/I/71V69L8mhXL._AC_UL320_.jpg", 
                        "Books", true));
                
                // Home & Kitchen
                productRepository.save(new Product(null, "B08L5VT3W9", "Instant Pot Duo Plus", 
                        "9-in-1 Electric Pressure Cooker, 6 Quart", 
                        new BigDecimal("99.95"), 75, 
                        "https://m.media-amazon.com/images/I/71V1fBFLkXL._AC_SL1500_.jpg", 
                        "Home & Kitchen", true));
                
                productRepository.save(new Product(null, "B08Q3M7ND5", "Ninja Air Fryer", 
                        "4-in-1 with Roast, Reheat & Dehydrate, 4 Quart", 
                        new BigDecimal("79.99"), 120, 
                        "https://m.media-amazon.com/images/I/71Ibq7dN1eL._AC_SL1500_.jpg", 
                        "Home & Kitchen", true));
                
                // Sports & Outdoors
                productRepository.save(new Product(null, "B07QLWT1HK", "YETI Rambler", 
                        "30 oz Stainless Steel Vacuum Insulated Tumbler", 
                        new BigDecimal("37.99"), 200, 
                        "https://m.media-amazon.com/images/I/71S4SJOWJoL._AC_SL1500_.jpg", 
                        "Sports & Outdoors", true));
                
                productRepository.save(new Product(null, "B08DFNX9WW", "Resistance Bands Set", 
                        "Exercise Bands with Handles, Ankle Straps, Door Anchor", 
                        new BigDecimal("29.99"), 300, 
                        "https://m.media-amazon.com/images/I/71W3T+yB3tL._AC_SL1500_.jpg", 
                        "Sports & Outdoors", true));
                
                // Fashion
                productRepository.save(new Product(null, "B07ZPNV2VM", "Levi's Men's 501 Original Jeans", 
                        "Classic straight leg fit", 
                        new BigDecimal("69.50"), 180, 
                        "https://m.media-amazon.com/images/I/71L40LlIRVL._AC_UX569_.jpg", 
                        "Fashion", true));
                
                productRepository.save(new Product(null, "B0C8QZV7YK", "Women's Classic Leather Handbag", 
                        "Designer Shoulder Bag with Adjustable Strap", 
                        new BigDecimal("89.99"), 95, 
                        "https://m.media-amazon.com/images/I/81+2VL9zZTL._AC_UL1500_.jpg", 
                        "Fashion", true));
                
                // Beauty & Personal Care
                productRepository.save(new Product(null, "B00VQX1J6K", "CeraVe Moisturizing Cream", 
                        "Daily Face and Body Moisturizer for Dry Skin, 19 oz", 
                        new BigDecimal("18.49"), 400, 
                        "https://m.media-amazon.com/images/I/71A9LY8qQEL._SL1500_.jpg", 
                        "Beauty & Personal Care", true));
                
                System.out.println("✓ Created 12 products with fake ASINs");
            }
        };
    }
}
