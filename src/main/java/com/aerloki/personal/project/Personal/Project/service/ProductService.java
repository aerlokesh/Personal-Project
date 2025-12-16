package com.aerloki.personal.project.Personal.Project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerloki.personal.project.Personal.Project.model.Product;
import com.aerloki.personal.project.Personal.Project.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductCacheService productCacheService;
    
    public List<Product> getAllProducts() {
        // 🔍 Check Redis cache first
        Optional<List<Product>> cachedProducts = productCacheService.getAllProductsFromCache();
        if (cachedProducts.isPresent()) {
            // ✅ Found in cache - no database query!
            return cachedProducts.get();
        }
        
        // ❌ Not in cache - query database
        List<Product> products = productRepository.findAll();
        
        // 💾 Save to cache for next time
        productCacheService.saveAllProductsToCache(products);
        
        return products;
    }
    
    public List<Product> getAvailableProducts() {
        // 🔍 Check Redis cache first
        Optional<List<Product>> cachedProducts = productCacheService.getAllProductsFromCache();
        if (cachedProducts.isPresent()) {
            // ✅ Found in cache - filter available products
            return cachedProducts.get().stream()
                .filter(Product::getAvailable)
                .toList();
        }
        
        // ❌ Not in cache - query database
        List<Product> products = productRepository.findByAvailable(true);
        
        // 💾 Cache all products (not just available ones)
        List<Product> allProducts = productRepository.findAll();
        productCacheService.saveAllProductsToCache(allProducts);
        
        return products;
    }
    
    public Optional<Product> getProductById(Long id) {
        // 🔍 Check Redis cache first
        Optional<Product> cachedProduct = productCacheService.getProductFromCache(id);
        if (cachedProduct.isPresent()) {
            // ✅ Found in cache - no database query!
            return cachedProduct;
        }
        
        // ❌ Not in cache - query database
        Optional<Product> product = productRepository.findById(id);
        
        // 💾 Save to cache for next time
        product.ifPresent(productCacheService::saveProductToCache);
        
        return product;
    }
    
    public Optional<Product> getProductByAsin(String asin) {
        // Direct database query (ASIN lookups are less frequent)
        return productRepository.findByAsin(asin);
    }
    
    public List<Product> getProductsByCategory(String category) {
        // Direct database query (category searches are dynamic)
        return productRepository.findByCategory(category);
    }
    
    public List<Product> searchProducts(String keyword) {
        // Direct database query (searches are dynamic and varied)
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    public Product saveProduct(Product product) {
        // Save to database
        Product savedProduct = productRepository.save(product);
        
        // 💾 Update cache
        productCacheService.saveProductToCache(savedProduct);
        
        // 🗑️  Invalidate all products list cache (it changed)
        productCacheService.invalidateAllProductsCache();
        
        return savedProduct;
    }
    
    public void deleteProduct(Long id) {
        // Delete from database
        productRepository.deleteById(id);
        
        // 🗑️  Invalidate caches
        productCacheService.invalidateProductCache(id);
        productCacheService.invalidateAllProductsCache();
    }
}
