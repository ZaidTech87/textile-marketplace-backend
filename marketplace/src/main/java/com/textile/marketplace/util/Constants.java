package com.textile.marketplace.util;

import java.math.BigDecimal;

public class Constants {

    public static final String BASE_URL = "/api";
    public static final String AUTH_URL = BASE_URL + "/auth";
    public static final String PRODUCT_URL = BASE_URL + "/products";
    public static final String PAYMENT_URL = BASE_URL + "/payments";
    public static final String CHAT_URL = BASE_URL + "/chat";
    public static final String USER_URL = BASE_URL + "/users";

    // OTP
    public static final int OTP_EXPIRY_MINUTES = 10;
    public static final int OTP_LENGTH = 6;

    // Payment
    public static final BigDecimal LISTING_FEE = new BigDecimal("9.00");

    // Product
    public static final int MAX_PRODUCT_IMAGES = 5;
    public static final int MAX_IMAGE_SIZE_MB = 10;

    // Chat
    public static final int MAX_CHAT_MESSAGE_LENGTH = 1000;
}