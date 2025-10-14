package com.yushan.backend.dto;

import lombok.Data;

@Data
public class SearchRequestDTO {
    private String keyword;           // Search keyword for title/username
    private String category;          // Category filter for novels
    private Integer page = 1;         // Page number (default: 1)
    private Integer pageSize = 10;    // Items per page (default: 10)
    private String sortBy = "created_at"; // Sort field
    private String sortOrder = "DESC";    // Sort order (ASC/DESC)
}
