package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListResponseDTO {

    private List<CategoryResponseDTO> categories;
    private int totalCount;

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
