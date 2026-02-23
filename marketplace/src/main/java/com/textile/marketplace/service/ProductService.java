package com.textile.marketplace.service;

import com.textile.marketplace.dto.request.ProductRequest;
import com.textile.marketplace.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createDraftProduct(ProductRequest request, Long sellerId);

    ProductResponse uploadProductImages(Long productId, MultipartFile coverImage, List<MultipartFile> additionalImages);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getAllActiveProducts(Pageable pageable);

    Page<ProductResponse> filterProducts(String category, String fabricType, String qualityGrade,
                                         BigDecimal minPrice, BigDecimal maxPrice, String city,
                                         String area, Pageable pageable);

    Page<ProductResponse> getSellerProducts(Long sellerId, Pageable pageable);

    ProductResponse updateProduct(Long id, ProductRequest request, Long sellerId);

    void deleteProduct(Long id, Long sellerId);

    ProductResponse incrementViews(Long id);

    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    Page<ProductResponse> getNearbyProducts(String city, String area, Pageable pageable);

    List<ProductResponse> getFeaturedProducts();
}