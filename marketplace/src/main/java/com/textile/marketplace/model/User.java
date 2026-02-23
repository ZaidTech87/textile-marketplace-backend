package com.textile.marketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 15)
    private String mobileNumber;

    private String name;

    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    private String profileImage;
    private String city;
    private String localArea;

    private Boolean isVerified = false;
    private Boolean isActive = true;
    private LocalDateTime verificationDate;

    // Seller specific
    private String gstNumber;
    private String businessAddress;
    private String businessDescription;

    private Double rating = 0.0;
    private Integer totalReviews = 0;
    private Integer totalProducts = 0;

    private LocalDateTime lastOtpRequest;
    private LocalDateTime lastLogin;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum UserType {
        SELLER, BUYER
    }
}