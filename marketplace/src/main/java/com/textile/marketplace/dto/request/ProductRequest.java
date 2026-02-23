package com.textile.marketplace.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String category;
    private String subCategory;

    // Textile specific
    private String fabricType;
    private String qualityGrade;
    private String loomType;
    private String designPattern;
    private String color;
    private Integer widthInInches;
    private Integer weightGsm;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private BigDecimal price;

    private String priceUnit = "meter";

    @Min(value = 1, message = "MOQ must be at least 1")
    private Integer moq = 1;

    private Integer stockQuantity = 0;
    private Boolean isStockUnlimited = false;
}