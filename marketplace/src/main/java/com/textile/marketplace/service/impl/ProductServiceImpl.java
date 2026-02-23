package com.textile.marketplace.service.impl;

import com.textile.marketplace.dto.request.ProductRequest;
import com.textile.marketplace.dto.response.ProductResponse;
import com.textile.marketplace.model.Product;
import com.textile.marketplace.model.User;
import com.textile.marketplace.repository.PaymentRepository;
import com.textile.marketplace.repository.ProductRepository;
import com.textile.marketplace.repository.UserRepository;
import com.textile.marketplace.service.CloudinaryService;
import com.textile.marketplace.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public ProductResponse createDraftProduct(ProductRequest request, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Create product in PENDING_PAYMENT status
        Product product = new Product();
        product.setSeller(seller);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setSubCategory(request.getSubCategory());
        product.setFabricType(request.getFabricType());
        product.setQualityGrade(request.getQualityGrade());
        product.setLoomType(request.getLoomType());
        product.setDesignPattern(request.getDesignPattern());
        product.setColor(request.getColor());
        product.setWidthInInches(request.getWidthInInches());
        product.setWeightGsm(request.getWeightGsm());
        product.setPrice(request.getPrice());
        product.setPriceUnit(request.getPriceUnit());
        product.setMoq(request.getMoq());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsStockUnlimited(request.getIsStockUnlimited());
        product.setStatus(Product.ProductStatus.PENDING_PAYMENT);

        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse uploadProductImages(Long productId, MultipartFile coverImage, List<MultipartFile> additionalImages) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<String> imageUrls = new ArrayList<>();

        // Upload cover image
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverUrl = cloudinaryService.uploadImage(coverImage, "products/covers");
            product.setCoverImage(coverUrl);
            imageUrls.add(coverUrl);
        }

        // Upload additional images
        if (additionalImages != null && !additionalImages.isEmpty()) {
            List<String> additionalUrls = new ArrayList<>();
            for (MultipartFile image : additionalImages) {
                if (!image.isEmpty()) {
                    String url = cloudinaryService.uploadImage(image, "products/additional");
                    additionalUrls.add(url);
                    imageUrls.add(url);
                }
            }
            try {
                product.setAdditionalImages(objectMapper.writeValueAsString(additionalUrls));
            } catch (Exception e) {
                throw new RuntimeException("Failed to save image URLs");
            }
        }

        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Increment view count asynchronously (could be done via @Async)
        incrementViews(id);

        return ProductResponse.fromEntity(product);
    }

    @Override
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByStatus(Product.ProductStatus.ACTIVE, pageable)
                .map(ProductResponse::fromEntity);
    }

    @Override
    public Page<ProductResponse> filterProducts(String category, String fabricType, String qualityGrade,
                                                BigDecimal minPrice, BigDecimal maxPrice, String city,
                                                String area, Pageable pageable) {
        return productRepository.filterProducts(category, fabricType, qualityGrade,
                        minPrice, maxPrice, city, area, pageable)
                .map(ProductResponse::fromEntity);
    }

    @Override
    public Page<ProductResponse> getSellerProducts(Long sellerId, Pageable pageable) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return productRepository.findBySellerAndStatus(seller, Product.ProductStatus.ACTIVE, pageable)
                .map(ProductResponse::fromEntity);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verify ownership
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("You don't have permission to update this product");
        }

        // Update fields
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setSubCategory(request.getSubCategory());
        product.setFabricType(request.getFabricType());
        product.setQualityGrade(request.getQualityGrade());
        product.setLoomType(request.getLoomType());
        product.setDesignPattern(request.getDesignPattern());
        product.setColor(request.getColor());
        product.setWidthInInches(request.getWidthInInches());
        product.setWeightGsm(request.getWeightGsm());
        product.setPrice(request.getPrice());
        product.setPriceUnit(request.getPriceUnit());
        product.setMoq(request.getMoq());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsStockUnlimited(request.getIsStockUnlimited());

        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("You don't have permission to delete this product");
        }

        // Delete images from Cloudinary
        if (product.getCoverImage() != null) {
            String publicId = cloudinaryService.extractPublicIdFromUrl(product.getCoverImage());
            cloudinaryService.deleteImage(publicId);
        }

        if (product.getAdditionalImages() != null) {
            try {
                List<String> additionalUrls = objectMapper.readValue(
                        product.getAdditionalImages(),
                        new TypeReference<List<String>>() {}
                );
                for (String url : additionalUrls) {
                    String publicId = cloudinaryService.extractPublicIdFromUrl(url);
                    cloudinaryService.deleteImage(publicId);
                }
            } catch (Exception e) {
                // Log error but continue
            }
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponse incrementViews(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setViews(product.getViews() + 1);
        productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(ProductResponse::fromEntity);
    }

    @Override
    public Page<ProductResponse> getNearbyProducts(String city, String area, Pageable pageable) {
        return productRepository.findNearbyProducts(city, area, pageable)
                .map(ProductResponse::fromEntity);
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findFeaturedProducts().stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
}