package com.textile.marketplace.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface CloudinaryService {

    String uploadImage(MultipartFile file, String folderName);

    String uploadImage(byte[] fileBytes, String fileName, String folderName);

    Map uploadVideo(MultipartFile file, String folderName);

    void deleteImage(String publicId);

    String getOptimizedImageUrl(String publicId, int width, int height);

    String extractPublicIdFromUrl(String imageUrl);
}