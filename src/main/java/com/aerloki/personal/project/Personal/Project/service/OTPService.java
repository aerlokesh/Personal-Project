package com.aerloki.personal.project.Personal.Project.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerloki.personal.project.Personal.Project.model.OTP;
import com.aerloki.personal.project.Personal.Project.repository.OTPRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {
    
    private final OTPRepository otpRepository;
    private final NotificationService notificationService;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate and send OTP to user's email
     */
    @Transactional
    public void generateAndSendOTP(String email) {
        // Delete any existing unverified OTPs for this email
        otpRepository.deleteByEmail(email);
        
        // Generate new OTP
        String otpCode = generateOTPCode();
        
        // Save OTP to database
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setVerified(false);
        
        otpRepository.save(otp);
        log.info("OTP generated for email: {}", email);
        
        // Send OTP via email
        notificationService.sendOTPEmail(email, otpCode);
    }
    
    /**
     * Verify OTP code
     */
    @Transactional
    public boolean verifyOTP(String email, String otpCode) {
        Optional<OTP> otpOptional = otpRepository.findByEmailAndOtpCodeAndVerifiedFalse(email, otpCode);
        
        if (otpOptional.isEmpty()) {
            log.warn("OTP verification failed - OTP not found for email: {}", email);
            return false;
        }
        
        OTP otp = otpOptional.get();
        
        // Check if OTP has expired
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("OTP verification failed - OTP expired for email: {}", email);
            return false;
        }
        
        // Mark OTP as verified
        otp.setVerified(true);
        otp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otp);
        
        log.info("OTP verified successfully for email: {}", email);
        return true;
    }
    
    /**
     * Check if email has a verified OTP (for signup completion)
     */
    public boolean hasVerifiedOTP(String email) {
        Optional<OTP> otpOptional = otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(email);
        return otpOptional.isEmpty() || otpOptional.get().isVerified();
    }
    
    /**
     * Generate a random OTP code
     */
    private String generateOTPCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    /**
     * Scheduled task to clean up expired OTPs
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredOTPs() {
        LocalDateTime now = LocalDateTime.now();
        otpRepository.deleteByExpiresAtBefore(now);
        log.info("Cleaned up expired OTPs");
    }
}
