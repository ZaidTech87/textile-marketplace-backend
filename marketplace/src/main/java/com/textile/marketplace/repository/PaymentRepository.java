package com.textile.marketplace.repository;

import com.textile.marketplace.model.Payment;
import com.textile.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String orderId);

    Optional<Payment> findByRazorpayPaymentId(String paymentId);

    List<Payment> findByUser(User user);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    Optional<Payment> findByProductId(Long productId);

    boolean existsByProductIdAndStatus(Long productId, Payment.PaymentStatus status);
}