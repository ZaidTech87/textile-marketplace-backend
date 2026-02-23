package com.textile.marketplace.service;

import com.textile.marketplace.dto.request.PaymentRequest;
import com.textile.marketplace.dto.response.PaymentResponse;
import com.razorpay.Order;

public interface PaymentService {

    Order createListingFeeOrder(Long productId, Long userId);

    PaymentResponse verifyPayment(PaymentRequest request, Long userId);

    PaymentResponse getPaymentStatus(String orderId);

    PaymentResponse getPaymentByProductId(Long productId);
}