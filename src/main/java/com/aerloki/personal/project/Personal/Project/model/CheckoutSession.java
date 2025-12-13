package com.aerloki.personal.project.Personal.Project.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

@Component
@SessionScope
@Data
public class CheckoutSession implements Serializable {
    
    // Step 1: Address
    private String fullName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country = "United States";
    
    // Step 2: Payment
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, UPI, COD, NET_BANKING
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private String upiId;
    
    // Tracking
    private int currentStep = 1; // 1=Address, 2=Payment, 3=Review
    
    public void reset() {
        this.fullName = null;
        this.phoneNumber = null;
        this.addressLine1 = null;
        this.addressLine2 = null;
        this.city = null;
        this.state = null;
        this.zipCode = null;
        this.country = "United States";
        this.paymentMethod = null;
        this.cardNumber = null;
        this.cardHolderName = null;
        this.expiryDate = null;
        this.cvv = null;
        this.upiId = null;
        this.currentStep = 1;
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null) sb.append(addressLine1);
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(addressLine2);
        }
        if (city != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (state != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (zipCode != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(zipCode);
        }
        if (country != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }
    
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
