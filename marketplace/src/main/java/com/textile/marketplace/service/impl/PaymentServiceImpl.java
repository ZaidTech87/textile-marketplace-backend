package com.textile.marketplace.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.textile.marketplace.dto.request.PaymentRequest;
import com.textile.marketplace.dto.response.PaymentResponse;
import com.textile.marketplace.model.Payment;
import com.textile.marketplace.model.Product;
import com.textile.marketplace.model.User;
import com.textile.marketplace.repository.PaymentRepository;
import com.textile.marketplace.repository.ProductRepository;
import com.textile.marketplace.repository.UserRepository;
import com.textile.marketplace.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RazorpayClient razorpayClient;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @Override
    @Transactional
    public Order createListingFeeOrder(Long productId, Long userId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", 900); // ₹9.00 in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "product_" + productId);

            Map<String, String> notes = new HashMap<>();
            notes.put("productId", productId.toString());
            notes.put("userId", userId.toString());
            notes.put("type", "listing_fee");
            orderRequest.put("notes", notes);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            // Save payment record
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setProduct(product);
            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            payment.setAmount(new BigDecimal("9.00"));
            payment.setStatus(Payment.PaymentStatus.CREATED);
            payment.setPaymentType(Payment.PaymentType.LISTING_FEE);
            paymentRepository.save(payment);

            return razorpayOrder;

        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(PaymentRequest request, Long userId) {
        try {
            // Verify signature
            boolean isValid = verifySignature(
                    request.getRazorpayOrderId(),
                    request.getRazorpayPaymentId(),
                    request.getRazorpaySignature()
            );

            if (!isValid) {
                throw new RuntimeException("Invalid payment signature");
            }

            // Get payment record
            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Update payment
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus(Payment.PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentResponse(request.getPaymentResponse());
            paymentRepository.save(payment);

            // Update product status to ACTIVE
            if (payment.getProduct() != null) {
                Product product = payment.getProduct();
                product.setStatus(Product.ProductStatus.ACTIVE);
                product.setPublishedAt(LocalDateTime.now());
                productRepository.save(product);
            }

            return PaymentResponse.fromEntity(payment);

        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse getPaymentStatus(String orderId) {
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return PaymentResponse.fromEntity(payment);
    }

    @Override
    public PaymentResponse getPaymentByProductId(Long productId) {
        Payment payment = paymentRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Payment not found for this product"));
        return PaymentResponse.fromEntity(payment);
    }

    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpaySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes());
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }
}