package com.textile.marketplace.service.impl;

import com.textile.marketplace.dto.response.UserResponse;
import com.textile.marketplace.model.User;
import com.textile.marketplace.repository.UserRepository;
import com.textile.marketplace.service.CloudinaryService;
import com.textile.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse getUserByMobile(String mobile) {
        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("User not found with mobile: " + mobile));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update basic info
        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }

        if (userDetails.getBusinessName() != null) {
            user.setBusinessName(userDetails.getBusinessName());
        }

        if (userDetails.getCity() != null) {
            user.setCity(userDetails.getCity());
        }

        if (userDetails.getLocalArea() != null) {
            user.setLocalArea(userDetails.getLocalArea());
        }

        // Seller specific fields
        if (user.getUserType() == User.UserType.SELLER) {
            if (userDetails.getGstNumber() != null) {
                user.setGstNumber(userDetails.getGstNumber());
            }

            if (userDetails.getBusinessAddress() != null) {
                user.setBusinessAddress(userDetails.getBusinessAddress());
            }

            if (userDetails.getBusinessDescription() != null) {
                user.setBusinessDescription(userDetails.getBusinessDescription());
            }
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse uploadProfileImage(Long id, MultipartFile image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Delete old image if exists
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            try {
                String publicId = cloudinaryService.extractPublicIdFromUrl(user.getProfileImage());
                if (publicId != null) {
                    cloudinaryService.deleteImage(publicId);
                }
            } catch (Exception e) {
                // Log error but continue with upload
                System.err.println("Failed to delete old image: " + e.getMessage());
            }
        }

        // Upload new image
        String imageUrl = cloudinaryService.uploadImage(image, "profiles");
        user.setProfileImage(imageUrl);

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    public List<UserResponse> getLocalSellers(String city, String area) {
        List<User> sellers;

        if (area != null && !area.isEmpty()) {
            // Get sellers from specific area
            sellers = userRepository.findSellersByArea(area);
        } else {
            // Get all sellers from city
            sellers = userRepository.findLocalSellers(city);
        }

        return sellers.stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Override
    public List<UserResponse> getTopRatedSellers() {
        return userRepository.findTopRatedSellers().stream()
                .map(UserResponse::fromEntity)
                .limit(10)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateRating(Long id, Double newRating) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update average rating
        Double currentRating = user.getRating() != null ? user.getRating() : 0.0;
        Integer totalReviews = user.getTotalReviews() != null ? user.getTotalReviews() : 0;

        // Calculate new average
        Double updatedRating = ((currentRating * totalReviews) + newRating) / (totalReviews + 1);

        user.setRating(updatedRating);
        user.setTotalReviews(totalReviews + 1);

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    public Long getTotalSellersCount() {
        return userRepository.countByUserType(User.UserType.SELLER);
    }

    @Override
    public List<UserResponse> searchSellers(String keyword) {
        return userRepository.searchSellers(keyword).stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse verifySeller(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (user.getUserType() != User.UserType.SELLER) {
            throw new RuntimeException("User is not a seller");
        }

        user.setIsVerified(true);
        user.setVerificationDate(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActive(false);

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    public boolean isMobileRegistered(String mobile) {
        return userRepository.existsByMobileNumber(mobile);
    }
}