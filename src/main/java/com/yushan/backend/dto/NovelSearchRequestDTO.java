package com.yushan.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NovelSearchRequestDTO extends NovelPaginationRequestDTO {
    
    private Integer categoryId;
    private String status;
    private String search;
    private String authorId;
    
    public NovelSearchRequestDTO() {
        super();
    }
    
    public NovelSearchRequestDTO(Integer page, Integer size, String sort, String order, 
                               Integer categoryId, String status, String search, String authorId) {
        super(page, size, sort, order);
        this.categoryId = categoryId;
        this.status = status;
        this.search = search;
        this.authorId = authorId;
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
        return authorId != null && !authorId.trim().isEmpty();
    }
}
