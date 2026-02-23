package com.textile.marketplace.service.impl;

import com.textile.marketplace.config.TwilioConfig;
import com.textile.marketplace.dto.request.OtpRequest;
import com.textile.marketplace.dto.request.OtpVerifyRequest;
import com.textile.marketplace.dto.response.OtpResponse;
import com.textile.marketplace.model.OtpVerification;
import com.textile.marketplace.repository.OtpRepository;
import com.textile.marketplace.service.OtpService;
import com.textile.marketplace.util.OtpGenerator;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private TwilioConfig twilioConfig;

    @Override
    @Transactional
    public OtpResponse generateAndSendOtp(OtpRequest request) {
        // Generate OTP
        String otpCode = OtpGenerator.generateOtp();

        // Save OTP to database
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setMobileNumber(request.getMobileNumber());
        otpVerification.setOtpCode(otpCode);
        otpVerification.setOtpType(OtpVerification.OtpType.LOGIN);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otpRepository.save(otpVerification);

        // Send SMS via Twilio (in production)
        sendSms(request.getMobileNumber(), otpCode);

        // For development, return OTP in response
        return OtpResponse.builder()
                .message("OTP sent successfully")
                .mobileNumber(request.getMobileNumber())
                .otpCode(otpCode) // Remove in production
                .expiresIn(600) // 10 minutes in seconds
                .build();
    }

    @Override
    @Transactional
    public boolean verifyOtp(OtpVerifyRequest request) {
        OtpVerification otpVerification = otpRepository.findValidOtp(
                request.getMobileNumber(),
                request.getOtpCode()
        ).orElse(null);

        if (otpVerification == null) {
            return false;
        }

        // Mark OTP as verified
        otpVerification.setIsVerified(true);
        otpRepository.save(otpVerification);

        return true;
    }

    @Override
    @Transactional
    public void resendOtp(String mobileNumber) {
        // Invalidate old OTPs
        // Generate and send new OTP
        OtpRequest request = new OtpRequest();
        request.setMobileNumber(mobileNumber);
        generateAndSendOtp(request);
    }

    private void sendSms(String mobileNumber, String otpCode) {
        try {
            // Format mobile number (add +91 for India)
            String formattedNumber = mobileNumber.startsWith("+") ?
                    mobileNumber : "+91" + mobileNumber;

            Message message = Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    "Your Textile B2B OTP is: " + otpCode + ". Valid for 10 minutes."
            ).create();

            System.out.println("📱 SMS sent: " + message.getSid());
        } catch (Exception e) {
            System.err.println("❌ Failed to send SMS: " + e.getMessage());
            // Log error but don't fail the flow (for development)
        }
    }
}