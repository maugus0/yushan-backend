package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CategoryListResponseDTO {

    private List<CategoryResponseDTO> categories;
    private int totalCount;


    public CategoryListResponseDTO(List<CategoryResponseDTO> categories, int totalCount) {
        this.setCategories(categories);
        this.totalCount = totalCount;
    }

    // Defensive copy methods to prevent exposing internal representation
    public List<CategoryResponseDTO> getCategories() {
        if (categories == null) {
            return null;
        }
        return new ArrayList<>(categories);
    }

    public void setCategories(List<CategoryResponseDTO> categories) {
        if (categories == null) {
            this.categories = null;
        } else {
            this.categories = new ArrayList<>(categories);
        }
    }

    /**
     * Create response from list of Category entities
     */
    public static CategoryListResponseDTO fromEntities(List<com.yushan.backend.entity.Category> categories) {
        if (categories == null) {
            return new CategoryListResponseDTO(List.of(), 0);
        }

        List<CategoryResponseDTO> dtoList = categories.stream()
                .map(CategoryResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new CategoryListResponseDTO(dtoList, categories.size());
    }

    /**
     * Create response for active categories only
     */
    public static CategoryListResponseDTO fromActiveEntities(List<com.yushan.backend.entity.Category> categories) {
        if (categories == null) {
            return new CategoryListResponseDTO(List.of(), 0);
        }

        List<CategoryResponseDTO> dtoList = categories.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .map(CategoryResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new CategoryListResponseDTO(dtoList, dtoList.size());
    }
}
