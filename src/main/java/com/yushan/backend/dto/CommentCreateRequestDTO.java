package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequestDTO {
    @NotNull(message = "chapterId must not be null")
    private Integer chapterId;

    @NotBlank(message = "content must not be blank")
    @Size(min = 1, max = 2000, message = "content must be between 1 and 2000 characters")
    private String content;

    private Boolean isSpoiler = false;
}
