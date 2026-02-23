package com.textile.marketplace.controller;

import com.razorpay.Order;
import com.textile.marketplace.dto.request.PaymentRequest;
import com.textile.marketplace.dto.response.PaymentResponse;
import com.textile.marketplace.security.JwtUtil;
import com.textile.marketplace.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create-order/{productId}")
    public ResponseEntity<Order> createListingFeeOrder(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        Order order = paymentService.createListingFeeOrder(productId, userId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        PaymentResponse response = paymentService.verifyPayment(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String orderId) {
        PaymentResponse response = paymentService.getPaymentStatus(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<PaymentResponse> getPaymentByProductId(@PathVariable Long productId) {
        PaymentResponse response = paymentService.getPaymentByProductId(productId);
        return ResponseEntity.ok(response);
    }
}