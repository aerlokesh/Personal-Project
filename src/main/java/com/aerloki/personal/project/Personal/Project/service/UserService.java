package com.aerloki.personal.project.Personal.Project.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerloki.personal.project.Personal.Project.model.User;
import com.aerloki.personal.project.Personal.Project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCacheService userCacheService;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    
    @Transactional
    public User registerUser(String email, String name, String password) {
        System.out.println("DEBUG - Attempting to register user:");
        System.out.println("  Email: [" + email + "]");
        System.out.println("  Name: [" + name + "]");
        
        if (userRepository.findByEmail(email).isPresent()) {
            System.out.println("  ERROR: User already exists with email: " + email);
            throw new RuntimeException("User already exists with email: " + email);
        }
        
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        
        System.out.println("  Encoded password length: " + user.getPassword().length());
        System.out.println("  Saving user to database...");
        
        User savedUser = userRepository.save(user);
        
        // 💾 Cache the newly registered user
        userCacheService.saveUserToCache(savedUser);
        
        System.out.println("  User saved successfully with ID: " + savedUser.getId());
        return savedUser;
    }
    
    public Optional<User> findByEmail(String email) {
        // 🔍 Step 1: Check Redis cache first (avoids database query)
        Optional<User> cachedUser = userCacheService.getUserFromCache(email);
        if (cachedUser.isPresent()) {
            // ✅ Found in cache - no database query needed!
            return cachedUser;
        }
        
        // ❌ Not in cache - query database
        Optional<User> userFromDb = userRepository.findByEmail(email);
        
        // 💾 If found in database, save to cache for next time
        userFromDb.ifPresent(userCacheService::saveUserToCache);
        
        return userFromDb;
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public Optional<User> getUserByEmail(String email) {
        return findByEmail(email);
    }
}
