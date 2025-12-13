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
    CommandLineRunner initDatabase(ProductRepository productRepository, 
                                   UserRepository userRepository) {
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
                
                // Additional Electronics
                productRepository.save(new Product(null, "B09B8V1M94", "Apple AirPods Pro (2nd Gen)", 
                        "Active Noise Cancellation, Adaptive Audio, Personalized Spatial Audio", 
                        new BigDecimal("249.00"), 85, 
                        "https://m.media-amazon.com/images/I/61SUj2aKoEL._AC_SL1500_.jpg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B0BSHF7WHW", "Samsung Galaxy Buds2 Pro", 
                        "Bluetooth Earbuds with Noise Cancelling, Hi-Fi Sound", 
                        new BigDecimal("229.99"), 110, 
                        "https://m.media-amazon.com/images/I/51b77T2JjQL._AC_SL1500_.jpg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B09V3JDT8T", "Apple Watch Series 9", 
                        "GPS 41mm Smart Watch with Fitness Tracker", 
                        new BigDecimal("399.00"), 60, 
                        "https://m.media-amazon.com/images/I/71xb2xkN5qL._AC_SL1500_.jpg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B0CHX1W1XY", "Anker Power Bank", 
                        "High-Speed Portable Charger, 20000mAh, USB-C", 
                        new BigDecimal("45.99"), 250, 
                        "https://m.media-amazon.com/images/I/61eL5B4tLnL._AC_SL1500_.jpg", 
                        "Electronics", true));
                
                productRepository.save(new Product(null, "B098RKWHHZ", "Logitech MX Master 3S", 
                        "Wireless Performance Mouse, Ergonomic, 8K DPI", 
                        new BigDecimal("99.99"), 140, 
                        "https://m.media-amazon.com/images/I/61ni3t1ryQL._AC_SL1500_.jpg", 
                        "Electronics", true));
                
                // Additional Books
                productRepository.save(new Product(null, "B08FF8FTKP", "The Psychology of Money", 
                        "Timeless lessons on wealth, greed, and happiness", 
                        new BigDecimal("17.99"), 420, 
                        "https://m.media-amazon.com/images/I/71TlZXbHJmL._AC_UL320_.jpg", 
                        "Books", true));
                
                productRepository.save(new Product(null, "B086WP794P", "Thinking, Fast and Slow", 
                        "Daniel Kahneman's groundbreaking work on decision making", 
                        new BigDecimal("19.99"), 380, 
                        "https://m.media-amazon.com/images/I/71+BeVCTGsL._AC_UL320_.jpg", 
                        "Books", true));
                
                productRepository.save(new Product(null, "B01K3V6K9G", "Sapiens: A Brief History of Humankind", 
                        "Yuval Noah Harari explores the history of our species", 
                        new BigDecimal("18.99"), 360, 
                        "https://m.media-amazon.com/images/I/71VXjMHvwqL._AC_UL320_.jpg", 
                        "Books", true));
                
                productRepository.save(new Product(null, "B07N1GV2LX", "Educated: A Memoir", 
                        "Tara Westover's powerful account of her journey", 
                        new BigDecimal("16.49"), 290, 
                        "https://m.media-amazon.com/images/I/71-jjT7DXQL._AC_UL320_.jpg", 
                        "Books", true));
                
                // Additional Home & Kitchen
                productRepository.save(new Product(null, "B0B3J5F3QT", "Vitamix E310 Explorian Blender", 
                        "Professional-Grade, 48 oz Container, Black", 
                        new BigDecimal("349.95"), 45, 
                        "https://m.media-amazon.com/images/I/71HCEhMb4VL._AC_SL1500_.jpg", 
                        "Home & Kitchen", true));
                
                productRepository.save(new Product(null, "B09BVXMRP5", "Keurig K-Elite Coffee Maker", 
                        "Single Serve K-Cup Pod with Iced Coffee Setting", 
                        new BigDecimal("169.99"), 90, 
                        "https://m.media-amazon.com/images/I/71W7dFhVXaL._AC_SL1500_.jpg", 
                        "Home & Kitchen", true));
                
                productRepository.save(new Product(null, "B08XQ22MGB", "KitchenAid Stand Mixer", 
                        "Artisan Series, 5-Quart, Multiple Colors Available", 
                        new BigDecimal("449.99"), 55, 
                        "https://m.media-amazon.com/images/I/81TZS2QkUOL._AC_SL1500_.jpg", 
                        "Home & Kitchen", true));
                
                productRepository.save(new Product(null, "B09NRVJ4FQ", "iRobot Roomba Robot Vacuum", 
                        "Self-Charging, Works with Alexa, Good for Pet Hair", 
                        new BigDecimal("274.99"), 70, 
                        "https://m.media-amazon.com/images/I/61YhAF5H8dL._AC_SL1500_.jpg", 
                        "Home & Kitchen", true));
                
                // Additional Sports & Outdoors
                productRepository.save(new Product(null, "B08T1M3YBY", "Hydro Flask Water Bottle", 
                        "Stainless Steel & Vacuum Insulated, 32 oz", 
                        new BigDecimal("44.95"), 210, 
                        "https://m.media-amazon.com/images/I/71RU0OLvhvL._AC_SL1500_.jpg", 
                        "Sports & Outdoors", true));
                
                productRepository.save(new Product(null, "B07PDHVT1Y", "Manduka PRO Yoga Mat", 
                        "Premium 6mm Thick Mat with Superior Cushion", 
                        new BigDecimal("128.00"), 130, 
                        "https://m.media-amazon.com/images/I/71f0uc6GxFL._AC_SL1500_.jpg", 
                        "Sports & Outdoors", true));
                
                productRepository.save(new Product(null, "B07TSXC7GH", "Fitbit Charge 6 Fitness Tracker", 
                        "Heart Rate, GPS, Sleep Tracking, 7 Days Battery", 
                        new BigDecimal("159.95"), 180, 
                        "https://m.media-amazon.com/images/I/71WMh3e7x1L._AC_SL1500_.jpg", 
                        "Sports & Outdoors", true));
                
                productRepository.save(new Product(null, "B08XQJJWYX", "TRX All-in-One Suspension Training", 
                        "Full Body Workouts, Includes Anchor, Workout Guide", 
                        new BigDecimal("169.95"), 95, 
                        "https://m.media-amazon.com/images/I/81uSM4VDDHL._AC_SL1500_.jpg", 
                        "Sports & Outdoors", true));
                
                // Additional Fashion
                productRepository.save(new Product(null, "B08N5M5GNY", "Nike Air Force 1 Sneakers", 
                        "Classic White Low-Top Basketball Shoes", 
                        new BigDecimal("110.00"), 160, 
                        "https://m.media-amazon.com/images/I/51i4aQW6zrL._AC_UY575_.jpg", 
                        "Fashion", true));
                
                productRepository.save(new Product(null, "B0B1JKQY3G", "Ray-Ban Wayfarer Sunglasses", 
                        "Classic Polarized UV Protection, Multiple Colors", 
                        new BigDecimal("154.00"), 125, 
                        "https://m.media-amazon.com/images/I/61BwG3wSpfL._AC_UX679_.jpg", 
                        "Fashion", true));
                
                productRepository.save(new Product(null, "B09PF7JN7L", "Timex Weekender Watch", 
                        "Casual Analog Quartz with Leather Strap", 
                        new BigDecimal("45.99"), 200, 
                        "https://m.media-amazon.com/images/I/81v+1EJqSsL._AC_UY879_.jpg", 
                        "Fashion", true));
                
                productRepository.save(new Product(null, "B0CGDFGH92", "Columbia Fleece Jacket", 
                        "Men's Full-Zip Soft Fleece with Classic Fit", 
                        new BigDecimal("55.00"), 140, 
                        "https://m.media-amazon.com/images/I/81dTaJX4y-L._AC_UX569_.jpg", 
                        "Fashion", true));
                
                // Additional Beauty & Personal Care
                productRepository.save(new Product(null, "B08TW7QDVH", "Dyson Supersonic Hair Dryer", 
                        "Fast Drying, Intelligent Heat Control, Professional", 
                        new BigDecimal("429.99"), 35, 
                        "https://m.media-amazon.com/images/I/51-BrdyW9PL._AC_SL1500_.jpg", 
                        "Beauty & Personal Care", true));
                
                productRepository.save(new Product(null, "B0B2FTZGVX", "Oral-B Electric Toothbrush", 
                        "Rechargeable with Pressure Sensor, Multiple Modes", 
                        new BigDecimal("89.99"), 175, 
                        "https://m.media-amazon.com/images/I/61jrBRGT3gL._AC_SL1500_.jpg", 
                        "Beauty & Personal Care", true));
                
                productRepository.save(new Product(null, "B09MJNQ5DK", "Neutrogena Hydro Boost", 
                        "Water Gel Face Moisturizer with Hyaluronic Acid", 
                        new BigDecimal("19.97"), 310, 
                        "https://m.media-amazon.com/images/I/71jLJr1xcBL._SL1500_.jpg", 
                        "Beauty & Personal Care", true));
                
                productRepository.save(new Product(null, "B07C4Q64V7", "The Ordinary Niacinamide Serum", 
                        "10% + Zinc 1%, Blemish Formula, 30ml", 
                        new BigDecimal("12.90"), 450, 
                        "https://m.media-amazon.com/images/I/51znEI1lBLL._SL1500_.jpg", 
                        "Beauty & Personal Care", true));
                
                // Gaming Category
                productRepository.save(new Product(null, "B0BXVHPL6N", "PlayStation 5 Console", 
                        "Ultra-High Speed SSD, Ray Tracing, 4K Gaming", 
                        new BigDecimal("499.99"), 25, 
                        "https://m.media-amazon.com/images/I/51erYPh66+L._AC_SL1024_.jpg", 
                        "Gaming", true));
                
                productRepository.save(new Product(null, "B09V3HJVL8", "Nintendo Switch OLED", 
                        "Portable Gaming Console with Vibrant 7-inch OLED Screen", 
                        new BigDecimal("349.99"), 80, 
                        "https://m.media-amazon.com/images/I/61-PblYntsL._AC_SL1500_.jpg", 
                        "Gaming", true));
                
                productRepository.save(new Product(null, "B0CHLZSXZ1", "Xbox Wireless Controller", 
                        "Carbon Black, Compatible with Xbox Series X|S", 
                        new BigDecimal("59.99"), 190, 
                        "https://m.media-amazon.com/images/I/51VWB7Aa2EL._AC_SL1500_.jpg", 
                        "Gaming", true));
                
                productRepository.save(new Product(null, "B0BTRHNZ6B", "SteelSeries Gaming Headset", 
                        "Arctis Nova 7 Wireless, Multi-Platform", 
                        new BigDecimal("179.99"), 115, 
                        "https://m.media-amazon.com/images/I/61Fl1lI8LYL._AC_SL1500_.jpg", 
                        "Gaming", true));
                
                System.out.println("✓ Created 40+ products with fake ASINs across multiple categories");
            }
        };
    }
}
