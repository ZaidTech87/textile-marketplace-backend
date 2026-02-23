package com.textile.marketplace.dto.mapper;

import com.textile.marketplace.dto.request.UserUpdateRequest;
import com.textile.marketplace.dto.response.UserResponse;
import com.textile.marketplace.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .mobileNumber(user.getMobileNumber())
                .name(user.getName())
                .businessName(user.getBusinessName())
                .userType(user.getUserType() != null ? user.getUserType().name() : null)
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

    /**
     * Convert list of User entities to list of UserResponse DTOs
     */
    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Update User entity from UserUpdateRequest
     */
    public void updateEntity(User user, UserUpdateRequest request) {
        if (request == null) {
            return;
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getBusinessName() != null) {
            user.setBusinessName(request.getBusinessName());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getLocalArea() != null) {
            user.setLocalArea(request.getLocalArea());
        }

        // Seller specific fields
        if (user.getUserType() == User.UserType.SELLER) {
            if (request.getGstNumber() != null) {
                user.setGstNumber(request.getGstNumber());
            }
            if (request.getBusinessAddress() != null) {
                user.setBusinessAddress(request.getBusinessAddress());
            }
            if (request.getBusinessDescription() != null) {
                user.setBusinessDescription(request.getBusinessDescription());
            }
        }
    }

    /**
     * Create a new User entity from OTP request
     */
    public User createFromOtp(String mobileNumber, User.UserType userType) {
        User user = new User();
        user.setMobileNumber(mobileNumber);
        user.setUserType(userType);
        user.setIsVerified(false);
        user.setIsActive(true);
        user.setRating(0.0);
        user.setTotalReviews(0);
        user.setTotalProducts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Create a profile response (for public view)
     */
    public UserResponse toPublicProfile(User user) {
        if (user == null) {
            return null;
        }

        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .businessName(user.getBusinessName())
                .profileImage(user.getProfileImage())
                .city(user.getCity())
                .localArea(user.getLocalArea())
                .rating(user.getRating())
                .totalReviews(user.getTotalReviews())
                .totalProducts(user.getTotalProducts())
                .isVerified(user.getIsVerified());

        // Add seller-specific fields only for sellers
        if (user.getUserType() == User.UserType.SELLER) {
            builder.businessDescription(user.getBusinessDescription())
                    .userType("SELLER");
        } else {
            builder.userType("BUYER");
        }

        // Don't include private info like mobile number, GST, etc.
        return builder.build();
    }

    /**
     * Create a seller card response (for marketplace listings)
     */
    public UserResponse toSellerCard(User seller) {
        if (seller == null || seller.getUserType() != User.UserType.SELLER) {
            return null;
        }

        return UserResponse.builder()
                .id(seller.getId())
                .name(seller.getName())
                .businessName(seller.getBusinessName())
                .profileImage(seller.getProfileImage())
                .city(seller.getCity())
                .localArea(seller.getLocalArea())
                .rating(seller.getRating())
                .totalReviews(seller.getTotalReviews())
                .totalProducts(seller.getTotalProducts())
                .isVerified(seller.getIsVerified())
                .businessDescription(seller.getBusinessDescription())
                .userType("SELLER")
                .build();
    }

    /**
     * Mark user as verified
     */
    public void markAsVerified(User user) {
        user.setIsVerified(true);
        user.setVerificationDate(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Update user rating after new review
     */
    public void updateRating(User user, Integer newRating) {
        if (user == null || newRating == null) {
            return;
        }

        Double currentRating = user.getRating() != null ? user.getRating() : 0.0;
        Integer totalReviews = user.getTotalReviews() != null ? user.getTotalReviews() : 0;

        // Calculate new average
        Double updatedRating = ((currentRating * totalReviews) + newRating) / (totalReviews + 1);

        user.setRating(updatedRating);
        user.setTotalReviews(totalReviews + 1);
        user.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Check if user is a seller
     */
    public boolean isSeller(User user) {
        return user != null && user.getUserType() == User.UserType.SELLER;
    }

    /**
     * Check if user is a buyer
     */
    public boolean isBuyer(User user) {
        return user != null && user.getUserType() == User.UserType.BUYER;
    }

    /**
     * Check if user is verified
     */
    public boolean isVerified(User user) {
        return user != null && Boolean.TRUE.equals(user.getIsVerified());
    }

    /**
     * Check if user is active
     */
    public boolean isActive(User user) {
        return user != null && Boolean.TRUE.equals(user.getIsActive());
    }
}