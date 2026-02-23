package com.textile.marketplace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 200, message = "Business name must not exceed 200 characters")
    private String businessName;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 200, message = "Local area must not exceed 200 characters")
    private String localArea;

    // Seller specific fields
    @Size(max = 20, message = "GST number must not exceed 20 characters")
    private String gstNumber;

    @Size(max = 500, message = "Business address must not exceed 500 characters")
    private String businessAddress;

    @Size(max = 1000, message = "Business description must not exceed 1000 characters")
    private String businessDescription;
}