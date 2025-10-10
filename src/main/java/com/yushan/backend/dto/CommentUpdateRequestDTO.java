package com.yushan.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentUpdateRequestDTO {
    @Size(min = 1, max = 2000, message = "content must be between 1 and 2000 characters")
    private String content;

    private Boolean isSpoiler;
}
