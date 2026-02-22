package com.textile.marketplace.dto.response;

import com.textile.marketplace.model.Payment;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private BigDecimal amount;
    private String currency;
    private String paymentType;
    private String paymentMethod;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    // Related entities
    private Long userId;
    private Long productId;

    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentType(payment.getPaymentType().name())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus().name())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .userId(payment.getUser().getId())
                .productId(payment.getProduct() != null ? payment.getProduct().getId() : null)
                .build();
    }
}