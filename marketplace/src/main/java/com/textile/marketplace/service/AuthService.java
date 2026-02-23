package com.textile.marketplace.service;

import com.textile.marketplace.dto.request.LoginRequest;
import com.textile.marketplace.dto.request.OtpRequest;
import com.textile.marketplace.dto.request.OtpVerifyRequest;
import com.textile.marketplace.dto.response.AuthResponse;
import com.textile.marketplace.dto.response.OtpResponse;

public interface AuthService {

    OtpResponse sendOtp(OtpRequest request);

    AuthResponse verifyOtpAndLogin(OtpVerifyRequest request);

    AuthResponse refreshToken(String refreshToken);

    void logout(String token);
}