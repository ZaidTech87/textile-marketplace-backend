package com.textile.marketplace.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.textile.marketplace.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String folderName) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "textile-b2b/" + folderName,
                            "public_id", UUID.randomUUID().toString(),
                            "overwrite", true
                    ));
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public String uploadImage(byte[] fileBytes, String fileName, String folderName) {
        try {
            Map uploadResult = cloudinary.uploader().upload(fileBytes,
                    ObjectUtils.asMap(
                            "folder", "textile-b2b/" + folderName,
                            "public_id", fileName,
                            "overwrite", true
                    ));
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public Map uploadVideo(MultipartFile file, String folderName) {
        try {
            return cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "textile-b2b/" + folderName,
                            "resource_type", "video",
                            "public_id", UUID.randomUUID().toString()
                    ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }

    @Override
    public String getOptimizedImageUrl(String publicId, int width, int height) {
        return cloudinary.url()
                .transformation(
                        new com.cloudinary.Transformation()
                                .width(width)
                                .height(height)
                                .crop("fill")
                                .quality("auto")
                                .fetchFormat("auto")
                )
                .generate(publicId);
    }

    @Override
    public String extractPublicIdFromUrl(String imageUrl) {
        // Example URL: https://res.cloudinary.com/cloud-name/image/upload/v1234567890/textile-b2b/products/image.jpg
        try {
            String[] parts = imageUrl.split("/");
            String lastPart = parts[parts.length - 1];
            return lastPart.split("\\.")[0]; // Remove extension
        } catch (Exception e) {
            return null;
        }
    }
}