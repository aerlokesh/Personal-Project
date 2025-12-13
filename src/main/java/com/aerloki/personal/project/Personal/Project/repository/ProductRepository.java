package com.aerloki.personal.project.Personal.Project.repository;

import com.aerloki.personal.project.Personal.Project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByAsin(String asin);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByAvailable(Boolean available);
    
    List<Product> findByNameContainingIgnoreCase(String name);
}
