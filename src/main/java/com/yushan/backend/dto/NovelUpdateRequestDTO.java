package com.yushan.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

    // New cover image as Base64 data URL (optional)
    @Pattern(regexp = "^data:image/(jpeg|jpg|png|gif|webp);base64,[A-Za-z0-9+/]+=*$", 
             message = "coverImgBase64 must be a valid Base64 data URL for image")
    private String coverImgBase64; // optional

    // New status string value (DRAFT/PUBLISHED/ARCHIVED) (optional)
    private String status; // optional, map to NovelStatus

    // Whether the novel is completed (optional)
    private Boolean isCompleted; // optional
}


