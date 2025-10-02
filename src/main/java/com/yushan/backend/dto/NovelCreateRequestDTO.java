package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

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

    // Cover image URL (optional)
    @URL(message = "coverImgUrl must be a valid URL")
    private String coverImgUrl;

    // Whether the novel is completed (optional)
    private Boolean isCompleted;
}


