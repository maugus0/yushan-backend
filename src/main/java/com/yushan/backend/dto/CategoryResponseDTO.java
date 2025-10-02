package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

    private Integer id;
    private String name;
    private String description;
    private String slug;
    private Boolean isActive;
    private Date createTime;
    private Date updateTime;

    /**
     * Convert Category entity to CategoryResponseDTO
     */
    public static CategoryResponseDTO fromEntity(com.yushan.backend.entity.Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getIsActive(),
                category.getCreateTime(),
                category.getUpdateTime()
        );
    }
}
