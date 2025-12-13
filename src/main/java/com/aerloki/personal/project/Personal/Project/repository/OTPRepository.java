package com.aerloki.personal.project.Personal.Project.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerloki.personal.project.Personal.Project.model.OTP;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    
    Optional<OTP> findByEmailAndOtpCodeAndVerifiedFalse(String email, String otpCode);
    
    Optional<OTP> findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(String email);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByEmail(String email);
}
