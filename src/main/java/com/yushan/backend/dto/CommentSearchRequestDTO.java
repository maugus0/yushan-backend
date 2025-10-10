package com.yushan.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentSearchRequestDTO {
    // Filtering
    private Integer chapterId;
    private Integer novelId;
    private UUID userId;
    private Boolean isSpoiler;
    private String search; // Search in content

    // Sorting
    private String sort = "createTime"; // createTime, likeCnt
    private String order = "desc"; // asc, desc

    // Pagination
    @Min(value = 0, message = "page must be at least 0")
    private int page = 0;

    @Min(value = 1, message = "size must be at least 1")
    @Max(value = 100, message = "size must be at most 100")
    private int size = 20;
}
