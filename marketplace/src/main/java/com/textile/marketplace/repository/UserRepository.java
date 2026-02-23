package com.textile.marketplace.repository;

import com.textile.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);

    List<User> findByUserType(User.UserType userType);

    @Query("SELECT u FROM User u WHERE u.city = :city AND u.userType = 'SELLER'")
    List<User> findLocalSellers(@Param("city") String city);

    @Query("SELECT u FROM User u WHERE u.localArea = :area AND u.userType = 'SELLER'")
    List<User> findSellersByArea(@Param("area") String area);

    @Query("SELECT u FROM User u ORDER BY u.rating DESC")
    List<User> findTopRatedSellers();
}