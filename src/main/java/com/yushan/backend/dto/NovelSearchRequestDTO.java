package com.yushan.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class NovelSearchRequestDTO {
    
    @Min(value = 0, message = "Page number must be >= 0")
    private Integer page = 0;
    
    @Min(value = 1, message = "Page size must be >= 1")
    @Max(value = 100, message = "Page size must be <= 100")
    private Integer size = 10;
    
    private String sort = "createTime";
    
    private String order = "desc";
    
    private Integer categoryId;
    private String status;
    private String search;
    private String authorName;
    
    public NovelSearchRequestDTO() {
        this.page = 0;
        this.size = 10;
        this.sort = "createTime";
        this.order = "desc";
    }
    
    public NovelSearchRequestDTO(Integer page, Integer size, String sort, String order, 
                               Integer categoryId, String status, String search, String authorName) {
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
        this.sort = sort != null ? sort : "createTime";
        this.order = order != null ? order : "desc";
        this.categoryId = categoryId;
        this.status = status;
        this.search = search;
        this.authorName = authorName;
    }
    
    public boolean hasCategoryFilter() {
        return categoryId != null && categoryId > 0;
    }
    
    public boolean hasStatusFilter() {
        return status != null && !status.trim().isEmpty();
    }
    
    public boolean hasSearchFilter() {
        return search != null && !search.trim().isEmpty();
    }
    
    public boolean hasAuthorFilter() {
        return authorName != null && !authorName.trim().isEmpty();
    }
    
    public boolean isAscending() {
        return "asc".equalsIgnoreCase(order);
    }
    
    public boolean isDescending() {
        return "desc".equalsIgnoreCase(order);
    }
}
