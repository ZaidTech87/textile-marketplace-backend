package com.textile.marketplace.repository;

import com.textile.marketplace.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByMobileNumberOrderByCreatedAtDesc(String mobileNumber);

    @Query("SELECT o FROM OtpVerification o WHERE o.mobileNumber = :mobile AND o.otpCode = :otp " +
            "AND o.isVerified = false AND o.expiresAt > CURRENT_TIMESTAMP")
    Optional<OtpVerification> findValidOtp(@Param("mobile") String mobile, @Param("otp") String otp);

    @Modifying
    @Transactional
    @Query("UPDATE OtpVerification o SET o.isVerified = true WHERE o.mobileNumber = :mobile AND o.otpCode = :otp")
    int markOtpAsVerified(@Param("mobile") String mobile, @Param("otp") String otp);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpVerification o WHERE o.expiresAt < :now")
    int deleteExpiredOtps(@Param("now") LocalDateTime now);
}