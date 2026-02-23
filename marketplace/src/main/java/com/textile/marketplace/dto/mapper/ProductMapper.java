package com.textile.marketplace.dto.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.textile.marketplace.dto.request.ProductRequest;
import com.textile.marketplace.dto.response.ProductResponse;
import com.textile.marketplace.model.Product;
import com.textile.marketplace.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert ProductRequest to Product entity
     */
    public Product toEntity(ProductRequest request, User seller) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setSeller(seller);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setSubCategory(request.getSubCategory());
        product.setFabricType(request.getFabricType());
        product.setQualityGrade(request.getQualityGrade());
        product.setLoomType(request.getLoomType());
        product.setDesignPattern(request.getDesignPattern());
        product.setColor(request.getColor());
        product.setWidthInInches(request.getWidthInInches());
        product.setWeightGsm(request.getWeightGsm());
        product.setPrice(request.getPrice());
        product.setPriceUnit(request.getPriceUnit());
        product.setMoq(request.getMoq());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsStockUnlimited(request.getIsStockUnlimited() != null ? request.getIsStockUnlimited() : false);

        // Set default status
        product.setStatus(Product.ProductStatus.PENDING_PAYMENT);
        product.setViews(0);
        product.setChatCount(0);
        product.setWishlistCount(0);
        product.setIsFeatured(false);
        product.setIsVerifiedProduct(false);

        return product;
    }

    /**
     * Update existing Product entity from ProductRequest
     */
    public void updateEntity(Product product, ProductRequest request) {
        if (request == null) {
            return;
        }

        if (request.getTitle() != null) {
            product.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getSubCategory() != null) {
            product.setSubCategory(request.getSubCategory());
        }
        if (request.getFabricType() != null) {
            product.setFabricType(request.getFabricType());
        }
        if (request.getQualityGrade() != null) {
            product.setQualityGrade(request.getQualityGrade());
        }
        if (request.getLoomType() != null) {
            product.setLoomType(request.getLoomType());
        }
        if (request.getDesignPattern() != null) {
            product.setDesignPattern(request.getDesignPattern());
        }
        if (request.getColor() != null) {
            product.setColor(request.getColor());
        }
        if (request.getWidthInInches() != null) {
            product.setWidthInInches(request.getWidthInInches());
        }
        if (request.getWeightGsm() != null) {
            product.setWeightGsm(request.getWeightGsm());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getPriceUnit() != null) {
            product.setPriceUnit(request.getPriceUnit());
        }
        if (request.getMoq() != null) {
            product.setMoq(request.getMoq());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getIsStockUnlimited() != null) {
            product.setIsStockUnlimited(request.getIsStockUnlimited());
        }
    }

    /**
     * Convert Product entity to ProductResponse DTO
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
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
                .videoUrl(product.getVideoUrl())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .isFeatured(product.getIsFeatured())
                .views(product.getViews())
                .chatCount(product.getChatCount())
                .wishlistCount(product.getWishlistCount())
                .sellerCity(product.getSellerCity())
                .sellerLocalArea(product.getSellerLocalArea())
                .createdAt(product.getCreatedAt())
                .publishedAt(product.getPublishedAt());

        // Parse additional images JSON
        if (product.getAdditionalImages() != null && !product.getAdditionalImages().isEmpty()) {
            try {
                List<String> additionalImages = objectMapper.readValue(
                        product.getAdditionalImages(),
                        new TypeReference<List<String>>() {}
                );
                builder.additionalImages(additionalImages);
            } catch (Exception e) {
                // Log error but continue
                System.err.println("Failed to parse additional images: " + e.getMessage());
            }
        }

        // Add seller info
        if (product.getSeller() != null) {
            builder.sellerId(product.getSeller().getId())
                    .sellerName(product.getSeller().getName())
                    .sellerBusinessName(product.getSeller().getBusinessName())
                    .sellerRating(product.getSeller().getRating())
                    .sellerTotalReviews(product.getSeller().getTotalReviews())
                    .sellerProfileImage(product.getSeller().getProfileImage());
        }

        return builder.build();
    }

    /**
     * Convert list of Product entities to list of ProductResponse DTOs
     */
    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Validate if seller owns the product
     */
    public boolean isProductOwner(Product product, Long sellerId) {
        return product != null &&
                product.getSeller() != null &&
                product.getSeller().getId().equals(sellerId);
    }

    /**
     * Set product images
     */
    public void setProductImages(Product product, String coverImage, List<String> additionalImages) {
        if (coverImage != null) {
            product.setCoverImage(coverImage);
        }

        if (additionalImages != null && !additionalImages.isEmpty()) {
            try {
                String imagesJson = objectMapper.writeValueAsString(additionalImages);
                product.setAdditionalImages(imagesJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save additional images", e);
            }
        }
    }

    /**
     * Create a summary response (for list views)
     */
    public ProductResponse toSummaryResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .price(product.getPrice())
                .priceUnit(product.getPriceUnit())
                .coverImage(product.getCoverImage())
                .qualityGrade(product.getQualityGrade())
                .fabricType(product.getFabricType())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .views(product.getViews())
                .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)
                .sellerName(product.getSeller() != null ? product.getSeller().getName() : null)
                .sellerBusinessName(product.getSeller() != null ? product.getSeller().getBusinessName() : null)
                .sellerRating(product.getSeller() != null ? product.getSeller().getRating() : null)
                .sellerCity(product.getSellerCity())
                .sellerLocalArea(product.getSellerLocalArea())
                .createdAt(product.getCreatedAt())
                .build();
    }

    /**
     * Convert list to summary responses
     */
    public List<ProductResponse> toSummaryResponseList(List<Product> products) {
        return products.stream()
                .map(this::toSummaryResponse)
                .toList();
    }
}