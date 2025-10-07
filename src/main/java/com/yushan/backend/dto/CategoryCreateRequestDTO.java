package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateRequestDTO {

    // Name of the category
    @NotBlank(message = "name must not be blank")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    // Description of the category (optional)
    @Size(max = 500, message = "description must be at most 500 characters")
    private String description;
}
