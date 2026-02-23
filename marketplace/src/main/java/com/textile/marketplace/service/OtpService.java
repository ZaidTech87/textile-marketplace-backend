package com.textile.marketplace.service;

import com.textile.marketplace.dto.request.OtpRequest;
import com.textile.marketplace.dto.request.OtpVerifyRequest;
import com.textile.marketplace.dto.response.OtpResponse;

public interface OtpService {

    OtpResponse generateAndSendOtp(OtpRequest request);

    boolean verifyOtp(OtpVerifyRequest request);

    void resendOtp(String mobileNumber);
}