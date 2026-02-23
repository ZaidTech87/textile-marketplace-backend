package com.textile.marketplace.service.impl;

import com.textile.marketplace.dto.request.LoginRequest;
import com.textile.marketplace.dto.request.OtpRequest;
import com.textile.marketplace.dto.request.OtpVerifyRequest;
import com.textile.marketplace.dto.response.AuthResponse;
import com.textile.marketplace.dto.response.OtpResponse;
import com.textile.marketplace.dto.response.UserResponse;
import com.textile.marketplace.model.User;
import com.textile.marketplace.repository.UserRepository;
import com.textile.marketplace.security.JwtUtil;
import com.textile.marketplace.service.AuthService;
import com.textile.marketplace.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public OtpResponse sendOtp(OtpRequest request) {
        // Check if user exists, if not create new user
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setMobileNumber(request.getMobileNumber());
                    newUser.setUserType(request.getUserType());
                    newUser.setIsVerified(false);
                    return userRepository.save(newUser);
                });

        // Update last OTP request time
        user.setLastOtpRequest(LocalDateTime.now());
        userRepository.save(user);

        // Generate and send OTP
        return otpService.generateAndSendOtp(request);
    }

    @Override
    @Transactional
    public AuthResponse verifyOtpAndLogin(OtpVerifyRequest request) {
        // Verify OTP
        boolean isValid = otpService.verifyOtp(request);

        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Get user
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user verification status
        if (!user.getIsVerified()) {
            user.setIsVerified(true);
            user.setVerificationDate(LocalDateTime.now());
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getMobileNumber(), user.getId(), user.getUserType().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getMobileNumber());

        // Build response
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .user(UserResponse.fromEntity(user))
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (jwtUtil.validateToken(refreshToken)) {
            String mobileNumber = jwtUtil.getMobileNumberFromToken(refreshToken);
            User user = userRepository.findByMobileNumber(mobileNumber)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newToken = jwtUtil.generateToken(user.getMobileNumber(), user.getId(), user.getUserType().name());

            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTime())
                    .user(UserResponse.fromEntity(user))
                    .build();
        }
        throw new RuntimeException("Invalid refresh token");
    }

    @Override
    public void logout(String token) {
        // In a real implementation, you might add the token to a blacklist
        SecurityContextHolder.clearContext();
    }
}