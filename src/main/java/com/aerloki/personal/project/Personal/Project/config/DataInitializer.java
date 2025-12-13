package com.aerloki.personal.project.Personal.Project.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aerloki.personal.project.Personal.Project.model.Product;
import com.aerloki.personal.project.Personal.Project.model.User;
import com.aerloki.personal.project.Personal.Project.repository.ProductRepository;
import com.aerloki.personal.project.Personal.Project.repository.UserRepository;

@Configuration
public class DataInitializer {
    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository, 
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Create fake users with encoded passwords
            if (userRepository.count() == 0) {
                userRepository.save(new User(null, "john.doe@example.com", "John Doe", 
                        passwordEncoder.encode("password123"), 
                        "123 Main St, Seattle, WA", "206-555-0100"));
                userRepository.save(new User(null, "jane.smith@example.com", "Jane Smith", 
                        passwordEncoder.encode("password123"), 
                        "456 Oak Ave, Portland, OR", "503-555-0200"));
                userRepository.save(new User(null, "bob.jones@example.com", "Bob Jones", 
                        passwordEncoder.encode("password123"), 
                        "789 Pine Rd, San Francisco, CA", "415-555-0300"));
                System.out.println("✓ Created 3 test users with encoded passwords");
            }
            
            // Create fake products with ASINs
            if (productRepository.count() == 0) {
                // Electronics
                productRepository.save(new Product(null, "B08N5WRWNW", "Echo Dot (4th Gen)", 
                        "Smart speaker with Alexa - Charcoal", 
                        new BigDecimal("49.99"), 150, 
                        "http://localhost:4566/product-images/product-1.svg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B0BXVHPL5M", "Fire TV Stick 4K Max", 
                        "Streaming device with Alexa Voice Remote", 
                        new BigDecimal("59.99"), 200, 
                        "http://localhost:4566/product-images/product-2.svg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B09B8RXYM5", "Kindle Paperwhite (11th Gen)", 
                        "Now with a 6.8\" display and adjustable warm light", 
                        new BigDecimal("139.99"), 100, 
                        "http://localhost:4566/product-images/product-3.svg", 
                        "Electronics", true));
                
                // Books
                productRepository.save(new Product(null, "B0C3JYL5ZM", "Atomic Habits", 
                        "An Easy & Proven Way to Build Good Habits & Break Bad Ones", 
                        new BigDecimal("16.99"), 500, 
                        "http://localhost:4566/product-images/product-4.svg", 
                        "Books", true));
                
                productRepository.save(new Product(null, "B07XJ8C8F7", "The Midnight Library", 
                        "A Novel by Matt Haig", 
                        new BigDecimal("14.99"), 350, 
                        "http://localhost:4566/product-images/product-5.svg", 
                        "Books", true));
                
                // Home & Kitchen
                productRepository.save(new Product(null, "B08L5VT3W9", "Instant Pot Duo Plus", 
                        "9-in-1 Electric Pressure Cooker, 6 Quart", 
                        new BigDecimal("99.95"), 75, 
                        "http://localhost:4566/product-images/product-6.svg", 
                        "Home & Kitchen", true));
                
                productRepository.save(new Product(null, "B08Q3M7ND5", "Ninja Air Fryer", 
                        "4-in-1 with Roast, Reheat & Dehydrate, 4 Quart", 
                        new BigDecimal("79.99"), 120, 
                        "http://localhost:4566/product-images/product-7.svg", 
                        "Home & Kitchen", true));
                
                // Sports & Outdoors
                productRepository.save(new Product(null, "B07QLWT1HK", "YETI Rambler", 
                        "30 oz Stainless Steel Vacuum Insulated Tumbler", 
                        new BigDecimal("37.99"), 200, 
                        "http://localhost:4566/product-images/product-8.svg", 
                        "Sports & Outdoors", true));
                
                productRepository.save(new Product(null, "B08DFNX9WW", "Resistance Bands Set", 
                        "Exercise Bands with Handles, Ankle Straps, Door Anchor", 
                        new BigDecimal("29.99"), 300, 
                        "http://localhost:4566/product-images/product-9.svg", 
                        "Sports & Outdoors", true));
                
                // Fashion
                productRepository.save(new Product(null, "B07ZPNV2VM", "Levi's Men's 501 Original Jeans", 
                        "Classic straight leg fit", 
                        new BigDecimal("69.50"), 180, 
                        "http://localhost:4566/product-images/product-10.svg", 
                        "Fashion", true));
                
                productRepository.save(new Product(null, "B0C8QZV7YK", "Women's Classic Leather Handbag", 
                        "Designer Shoulder Bag with Adjustable Strap", 
                        new BigDecimal("89.99"), 95, 
                        "http://localhost:4566/product-images/product-11.svg", 
                        "Fashion", true));
                
                // Beauty & Personal Care
                productRepository.save(new Product(null, "B00VQX1J6K", "CeraVe Moisturizing Cream", 
                        "Daily Face and Body Moisturizer for Dry Skin, 19 oz", 
                        new BigDecimal("18.49"), 400, 
                        "http://localhost:4566/product-images/product-12.svg", 
                        "Beauty & Personal Care", true));
                
                // Additional Electronics
                productRepository.save(new Product(null, "B09B8V1M94", "Apple AirPods Pro (2nd Gen)", 
                        "Active Noise Cancellation, Adaptive Audio, Personalized Spatial Audio", 
                        new BigDecimal("249.00"), 85, 
                        "http://localhost:4566/product-images/product-13.svg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B0BSHF7WHW", "Samsung Galaxy Buds2 Pro", 
                        "Bluetooth Earbuds with Noise Cancelling, Hi-Fi Sound", 
                        new BigDecimal("229.99"), 110, 
                        "http://localhost:4566/product-images/product-14.svg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B09V3JDT8T", "Apple Watch Series 9", 
                        "GPS 41mm Smart Watch with Fitness Tracker", 
                        new BigDecimal("399.00"), 60, 
                        "http://localhost:4566/product-images/product-15.svg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B0CHX1W1XY", "Anker Power Bank", 
                        "High-Speed Portable Charger, 20000mAh, USB-C", 
                        new BigDecimal("45.99"), 250, 
                        "http://localhost:4566/product-images/product-16.svg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B098RKWHHZ", "Logitech MX Master 3S", 
                        "Wireless Performance Mouse, Ergonomic, 8K DPI", 
                        new BigDecimal("99.99"), 140, 
                        "http://localhost:4566/product-images/product-17.svg", 
                        "Electronics", true));
                
                // Additional Books
                productRepository.save(new Product(null, "B08FF8FTKP", "The Psychology of Money", 
                        "Timeless lessons on wealth, greed, and happiness", 
                        new BigDecimal("17.99"), 420, 
                        "http://localhost:4566/product-images/product-18.svg", 
                        "Books", true));
                
                productRepository.save(new Product(null, "B086WP794P", "Thinking, Fast and Slow", 
                        "Daniel Kahneman's groundbreaking work", 
                        new BigDecimal("19.99"), 380, 
                        "http://localhost:4566/product-images/product-19.svg", 
                        "Books", true));
                
                // Additional Home & Kitchen
                productRepository.save(new Product(null, "B0B3J5F3QT", "Vitamix Blender", 
                        "Professional-Grade, 48 oz Container", 
                        new BigDecimal("349.95"), 45, 
                        "http://localhost:4566/product-images/product-20.svg", 
                        "Home & Kitchen", true));
                
                productRepository.save(new Product(null, "B09BVXMRP5", "Keurig Coffee Maker", 
                        "Single Serve K-Cup Pod", 
                        new BigDecimal("169.99"), 90, 
                        "http://localhost:4566/product-images/product-21.svg", 
                        "Home & Kitchen", true));
                
                // Additional Sports
                productRepository.save(new Product(null, "B08T1M3YBY", "Hydro Flask", 
                        "Stainless Steel Water Bottle, 32 oz", 
                        new BigDecimal("44.95"), 210, 
                        "http://localhost:4566/product-images/product-22.svg", 
                        "Sports & Outdoors", true));
                
                productRepository.save(new Product(null, "B07TSXC7GH", "Fitbit Tracker", 
                        "Heart Rate, GPS, Sleep Tracking", 
                        new BigDecimal("159.95"), 180, 
                        "http://localhost:4566/product-images/product-23.svg", 
                        "Sports & Outdoors", true));
                
                // Additional Fashion
                productRepository.save(new Product(null, "B08N5M5GNY", "Nike Sneakers", 
                        "Classic White Low-Top Basketball Shoes", 
                        new BigDecimal("110.00"), 160, 
                        "http://localhost:4566/product-images/product-24.svg", 
                        "Fashion", true));
                
                productRepository.save(new Product(null, "B0B1JKQY3G", "Ray-Ban Sunglasses", 
                        "Classic Polarized UV Protection", 
                        new BigDecimal("154.00"), 125, 
                        "http://localhost:4566/product-images/product-25.svg", 
                        "Fashion", true));
                
                // Beauty
                productRepository.save(new Product(null, "B08TW7QDVH", "Hair Dryer", 
                        "Fast Drying, Intelligent Heat Control", 
                        new BigDecimal("429.99"), 35, 
                        "http://localhost:4566/product-images/product-26.svg", 
                        "Beauty & Personal Care", true));
                
                productRepository.save(new Product(null, "B0B2FTZGVX", "Electric Toothbrush", 
                        "Rechargeable with Pressure Sensor", 
                        new BigDecimal("89.99"), 175, 
                        "http://localhost:4566/product-images/product-27.svg", 
                        "Beauty & Personal Care", true));
                
                // Gaming
                productRepository.save(new Product(null, "B0BXVHPL6N", "PlayStation 5", 
                        "Ultra-High Speed SSD, 4K Gaming", 
                        new BigDecimal("499.99"), 25, 
                        "http://localhost:4566/product-images/product-28.svg", 
                        "Gaming", true));
                
                productRepository.save(new Product(null, "B09V3HJVL8", "Nintendo Switch", 
                        "Portable Gaming Console", 
                        new BigDecimal("349.99"), 80, 
                        "http://localhost:4566/product-images/product-29.svg", 
                        "Gaming", true));
                
                // Toys
                productRepository.save(new Product(null, "B09JQBQXXX", "LEGO Set", 
                        "Building Blocks, 2000 Pieces", 
                        new BigDecimal("149.99"), 50, 
                        "http://localhost:4566/product-images/product-30.svg", 
                        "Toys & Games", true));
                
                // Pet Supplies
                productRepository.save(new Product(null, "B0B8Q6NF8Y", "Automatic Pet Feeder", 
                        "Smart Feed with App Control", 
                        new BigDecimal("149.99"), 80, 
                        "http://localhost:4566/product-images/product-31.svg", 
                        "Pet Supplies", true));
                
                // Tools
                productRepository.save(new Product(null, "B08NDBZ1LH", "Cordless Drill Kit", 
                        "20V with Battery and Charger", 
                        new BigDecimal("149.00"), 95, 
                        "http://localhost:4566/product-images/product-32.svg", 
                        "Tools & Home Improvement", true));
                
                // Automotive
                productRepository.save(new Product(null, "B09C5GNXK8", "Dash Cam", 
                        "1080p HD Camera with WiFi", 
                        new BigDecimal("129.99"), 110, 
                        "http://localhost:4566/product-images/product-33.svg", 
                        "Automotive", true));
                
                // Office
                productRepository.save(new Product(null, "B09HKH8B3Y", "Standing Desk", 
                        "Adjustable Height Workstation", 
                        new BigDecimal("159.99"), 75, 
                        "http://localhost:4566/product-images/product-34.svg", 
                        "Office Products", true));
                
                productRepository.save(new Product(null, "B08L8KDWJD", "Office Chair", 
                        "Ergonomic with Lumbar Support", 
                        new BigDecimal("199.99"), 60, 
                        "http://localhost:4566/product-images/product-35.svg", 
                        "Office Products", true));
                
                System.out.println("✓ Created 35 products with LocalStack S3 images");
            }
        };
    }
}
