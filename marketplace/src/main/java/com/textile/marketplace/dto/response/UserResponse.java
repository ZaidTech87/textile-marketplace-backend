package com.textile.marketplace.dto.response;

import com.textile.marketplace.model.User;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String mobileNumber;
    private String name;
    private String businessName;
    private String userType;
    private String profileImage;
    private String city;
    private String localArea;
    private Boolean isVerified;
    private String gstNumber;
    private String businessAddress;
    private String businessDescription;
    private Double rating;
    private Integer totalReviews;
    private Integer totalProducts;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .mobileNumber(user.getMobileNumber())
                .name(user.getName())
                .businessName(user.getBusinessName())
                .userType(user.getUserType().name())
                .profileImage(user.getProfileImage())
                .city(user.getCity())
                .localArea(user.getLocalArea())
                .isVerified(user.getIsVerified())
                .gstNumber(user.getGstNumber())
                .businessAddress(user.getBusinessAddress())
                .businessDescription(user.getBusinessDescription())
                .rating(user.getRating())
                .totalReviews(user.getTotalReviews())
                .totalProducts(user.getTotalProducts())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}