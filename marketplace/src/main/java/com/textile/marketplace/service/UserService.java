package com.textile.marketplace.service;

import com.textile.marketplace.dto.response.UserResponse;
import com.textile.marketplace.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse getUserByMobile(String mobile);

    UserResponse updateProfile(Long id, User userDetails);

    UserResponse uploadProfileImage(Long id, MultipartFile image);

    List<UserResponse> getLocalSellers(String city, String area);

    List<UserResponse> getTopRatedS