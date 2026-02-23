package com.textile.marketplace.repository;

import com.textile.marketplace.model.Product;
import com.textile.marketplace.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);

    List<Product> findBySeller(User seller);

    Page<Product> findBySellerAndStatus(User seller, Product.ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "p.status = 'ACTIVE' AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:fabricType IS NULL OR p.fabricType = :fabricType) AND " +
            "(:qualityGrade IS NULL OR p.qualityGrade = :qualityGrade) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:city IS NULL OR p.sellerCity = :city) AND " +
            "(:area IS NULL OR p.sellerLocalArea = :area)")
    Page<Product> filterProducts(@Param("category") String category,
                                 @Param("fabricType") String fabricType,
                                 @Param("qualityGrade") String qualityGrade,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("city") String city,
                                 @Param("area") String area,
                                 Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    Page<Product> findRecentProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.isFeatured = true")
    List<Product> findFeaturedProducts();

    @Query(value = "SELECT * FROM products WHERE status = 'ACTIVE' AND " +
            "MATCH(title, description) AGAINST(:keyword IN NATURAL LANGUAGE MODE)",
            nativeQuery = true)
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.seller.city = :city AND p.status = 'ACTIVE' " +
            "ORDER BY CASE WHEN p.seller.localArea = :area THEN 0 ELSE 1 END, p.createdAt DESC")
    Page<Product> findNearbyProducts(@Param("city") String city,
                                     @Param("area") String area,
                                     Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.seller.id = :sellerId AND p.status = 'ACTIVE'")
    long countActiveProductsBySeller(@Param("sellerId") Long sellerId);
}