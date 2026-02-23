package com.textile.marketplace.controller;

import com.textile.marketplace.dto.request.ProductRequest;
import com.textile.marketplace.dto.response.ProductResponse;
import com.textile.marketplace.security.JwtUtil;
import com.textile.marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/draft")
    public ResponseEntity<ProductResponse> createDraftProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        ProductResponse response = productService.createDraftProduct(request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductResponse> uploadProductImages(
            @PathVariable Long productId,
            @RequestParam("coverImage") MultipartFile coverImage,
            @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            @RequestHeader("Authorization") String token) {
        // Verify ownership in service
        ProductResponse response = productService.uploadProductImages(productId, coverImage, additionalImages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<ProductResponse>> getProductFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String fabricType,
            @RequestParam(required = false) String qualityGrade,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductResponse> products = productService.filterProducts(
                category, fabricType, qualityGrade, minPrice, maxPrice, city, area, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/nearby")
    public ResponseEntity<Page<ProductResponse>> getNearbyProducts(
            @RequestParam String city,
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getNearbyProducts(city, area, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<ProductResponse>> getSellerProducts(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getSellerProducts(sellerId, pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        ProductResponse response = productService.updateProduct(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.substring(7));
        productService.deleteProduct(id, userId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        List<ProductResponse> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products);
    }
}