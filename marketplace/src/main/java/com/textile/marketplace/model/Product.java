package com.textile.marketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String category;
    private String subCategory;

    // Textile specific
    private String fabricType;
    private String qualityGrade;
    private String loomType;
    private String designPattern;
    private String color;
    private Integer widthInInches;
    private Integer weightGsm;

    // Pricing
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String priceUnit = "meter";
    private Integer moq = 1;
    private Integer stockQuantity = 0;
    private Boolean isStockUnlimited = false;

    // Images
    private String coverImage;
    @Column(length = 2000)
    private String additionalImages; // JSON array

    private String videoUrl;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.PENDING_PAYMENT;

    private Boolean isFeatured = false;
    private Boolean isVerifiedProduct = false;

    private Integer views = 0;
    private Integer chatCount = 0;
    private Integer wishlistCount = 0;

    private String sellerCity;
    private String sellerLocalArea;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (seller != null) {
            sellerCity = seller.getCity();
            sellerLocalArea = seller.getLocalArea();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProductStatus {
        PENDING_PAYMENT, ACTIVE, INACTIVE, SOLD, REJECTED
    }
}