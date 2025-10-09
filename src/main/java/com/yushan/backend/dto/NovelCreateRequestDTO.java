package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class NovelCreateRequestDTO {
    // Title of the novel
    @NotBlank(message = "title must not be blank")
    private String title;

    // Short summary (max 4000 characters)
    @Size(max = 4000, message = "synopsis must be at most 4000 characters")
    private String synopsis;

    // Target category ID
    @NotNull(message = "categoryId must not be null")
    private Integer categoryId;

    // Cover image as Base64 data URL (optional)
    @Pattern(regexp = "^data:image/(jpeg|jpg|png|gif|webp);base64,[A-Za-z0-9+/]+=*$", 
             message = "coverImgBase64 must be a valid Base64 data URL for image")
    private String coverImgBase64;

    // Whether the novel is completed (optional)
    private Boolean isCompleted;
}


