package com.textile.marketplace.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserResponse user;
}