package com.yushan.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewUpdateRequestDTO {
    @Min(value = 1, message = "rating must be at least 1")
    @Max(value = 5, message = "rating must be at most 5")
    private Integer rating;

    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    @Size(max = 4000, message = "content must be at most 4000 characters")
    private String content;

    private Boolean isSpoiler;
}
