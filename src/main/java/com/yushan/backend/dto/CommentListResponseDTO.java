package com.yushan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponseDTO {
    private List<CommentResponseDTO> comments;
    private long totalCount;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
