package com.textile.marketplace.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpResponse {
    private String message;
    private String mobileNumber;
    private String otpCode; // Only for development
    private Integer expiresIn;
}