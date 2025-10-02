package com.yushan.backend.dto;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import lombok.Data;

@Data
public class NovelUpdateRequestDTO {
    // New title (optional)
    private String title; // optional

    // Updated synopsis (max 4000) (optional)
    @Size(max = 4000, message = "synopsis must be at most 4000 characters")
    private String synopsis; // optional

    // New category ID (optional)
    private Integer categoryId; // optional

    // New cover image URL (optional)
    @URL(message = "coverImgUrl must be a valid URL")
    private String coverImgUrl; // optional

    // New status string value (DRAFT/PUBLISHED/ARCHIVED) (optional)
    private String status; // optional, map to NovelStatus

    // Whether the novel is completed (optional)
    private Boolean isCompleted; // optional
}


