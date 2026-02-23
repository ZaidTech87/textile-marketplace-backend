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
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse getUserByMobile(String mobile) {
        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(userDetails.getName());
        user.setBusinessName(userDetails.getBusinessName());
        user.setCity(userDetails.getCity());
        user.setLocalArea(userDetails.getLocalArea());

        // Seller specific fields
        if (user.getUserType() == User.UserType.SELLER) {
            user.setGstNumber(userDetails.getGstNumber());
            user.setBusinessAddress(userDetails.getBusinessAddress());
            user.setBusinessDescription(userDetails.getBusinessDescription());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse uploadProfileImage(Long id, MultipartFile image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old image if exists
        if (user.getProfileImage() != null) {
            String publicId = cloudinaryService.extractPublicIdFromUrl(user.getProfileImage());
            cloudinaryService.deleteImage(publicId);
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
            sellers = userRepository.findSellersByArea(area);
        } else {
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
}