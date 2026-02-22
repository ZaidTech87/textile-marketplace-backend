package com.textile.marketplace.dto.response;

import com.textile.marketplace.model.Product;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String subCategory;
    private String fabricType;
    private String qualityGrade;
    private String loomType;
    private String designPattern;
    private String color;
    private Integer widthInInches;
    private Integer weightGsm;
    private BigDecimal price;
    private String priceUnit;
    private Integer moq;
    private Integer stockQuantity;
    private Boolean isStockUnlimited;
    private String coverImage;
    private List<String> additionalImages;
    private String videoUrl;
    private String status;
    private Boolean isFeatured;
    private Integer views;
    private Integer chatCount;
    private Integer wishlistCount;
    private String sellerCity;
    private String sellerLocalArea;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    // Seller info
    private Long sellerId;
    private String sellerName;
    private String sellerBusinessName;
    private Double sellerRating;
    private Integer sellerTotalReviews;
    private String sellerProfileImage;

    public static ProductResponse fromEntity(Product product) {
        List<String> additionalImagesList = null;
        if (product.getAdditionalImages() != null) {
            try {
                additionalImagesList = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(product.getAdditionalImages(),
                                new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
            } catch (Exception e) {
                // Ignore
            }
        }

        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .category(product.getCategory())
                .subCategory(product.getSubCategory())
                .fabricType(product.getFabricType())
                .qualityGrade(product.getQualityGrade())
                .loomType(product.getLoomType())
                .designPattern(product.getDesignPattern())
                .color(product.getColor())
                .widthInInches(product.getWidthInInches())
                .weightGsm(product.getWeightGsm())
                .price(product.getPrice())
                .priceUnit(product.getPriceUnit())
                .moq(product.getMoq())
                .stockQuantity(product.getStockQuantity())
                .isStockUnlimited(product.getIsStockUnlimited())
                .coverImage(product.getCoverImage())
                .additionalImages(additionalImagesList)
                .videoUrl(product.getVideoUrl())
                .status(product.getStatus().name())
                .isFeatured(product.getIsFeatured())
                .views(product.getViews())
                .chatCount(product.getChatCount())
                .wishlistCount(product.getWishlistCount())
                .sellerCity(product.getSellerCity())
                .sellerLocalArea(product.getSellerLocalArea())
                .createdAt(product.getCreatedAt())
                .publishedAt(product.getPublishedAt())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getName())
                .sellerBusinessName(product.getSeller().getBusinessName())
                .sellerRating(product.getSeller().getRating())
                .sellerTotalReviews(product.getSeller().getTotalReviews())
                .sellerProfileImage(product.getSeller().getProfileImage())
                .build();
    }
}