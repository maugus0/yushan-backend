package com.yushan.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequestDTO {

    // New name (optional)
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    // Updated description (optional)
    @Size(max = 500, message = "description must be at most 500 characters")
    private String description;

    // Whether the category is active (optional)
    private Boolean isActive;
}
