package com.yushan.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class NovelPaginationRequestDTO {
    
    @Min(value = 0, message = "Page number must be >= 0")
    private Integer page = 0;
    
    @Min(value = 1, message = "Page size must be >= 1")
    @Max(value = 100, message = "Page size must be <= 100")
    private Integer size = 10;
    
    private String sort = "createTime";
    
    private String order = "desc";
    
    public NovelPaginationRequestDTO() {
        this.page = 0;
        this.size = 10;
        this.sort = "createTime";
        this.order = "desc";
    }
    
    public NovelPaginationRequestDTO(Integer page, Integer size, String sort, String order) {
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
        this.sort = sort != null ? sort : "createTime";
        this.order = order != null ? order : "desc";
    }
    
    public boolean isAscending() {
        return "asc".equalsIgnoreCase(order);
    }
    
    public boolean isDescending() {
        return "desc".equalsIgnoreCase(order);
    }
}
