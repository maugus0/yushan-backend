package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "content")
public class PageResponseDTO<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    
    // Override getter and setter for content to use defensive copy
    public List<T> getContent() {
        return content != null ? new java.util.ArrayList<>(content) : new java.util.ArrayList<>();
    }
    
    public void setContent(List<T> content) {
        this.content = content != null ? new java.util.ArrayList<>(content) : new java.util.ArrayList<>();
    }
    
    public PageResponseDTO(List<T> content, long totalElements, int currentPage, int size) {
        this.content = content != null ? new java.util.ArrayList<>(content) : new java.util.ArrayList<>();
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = currentPage == 0;
        this.last = currentPage >= totalPages - 1;
        this.hasNext = !this.last;
        this.hasPrevious = !this.first;
    }
    
    public static <T> PageResponseDTO<T> of(List<T> content, long totalElements, int currentPage, int size) {
        return new PageResponseDTO(content, totalElements, currentPage, size);
    }
    
    // All-args constructor for Lombok compatibility
    public PageResponseDTO(List<T> content, long totalElements, int totalPages, int currentPage, int size, boolean first, boolean last, boolean hasNext, boolean hasPrevious) {
        this.content = content != null ? new java.util.ArrayList<>(content) : new java.util.ArrayList<>();
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.size = size;
        this.first = first;
        this.last = last;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }
    
}
