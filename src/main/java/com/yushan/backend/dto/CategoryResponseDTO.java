package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CategoryResponseDTO {

    private Integer id;
    private String name;
    private String description;
    private String slug;
    private Boolean isActive;
    private Date createTime;
    private Date updateTime;


    public CategoryResponseDTO(Integer id, String name, String description, String slug, Boolean isActive, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.isActive = isActive;
        this.setCreateTime(createTime);
        this.setUpdateTime(updateTime);
    }

    // Defensive copy methods to prevent exposing internal representation
    public Date getCreateTime() {
        if (createTime == null) {
            return null;
        }
        return new Date(createTime.getTime());
    }

    public void setCreateTime(Date createTime) {
        if (createTime == null) {
            this.createTime = null;
        } else {
            this.createTime = new Date(createTime.getTime());
        }
    }

    public Date getUpdateTime() {
        if (updateTime == null) {
            return null;
        }
        return new Date(updateTime.getTime());
    }

    public void setUpdateTime(Date updateTime) {
        if (updateTime == null) {
            this.updateTime = null;
        } else {
            this.updateTime = new Date(updateTime.getTime());
        }
    }

    /**
     * Convert Category entity to CategoryResponseDTO
     */
    public static CategoryResponseDTO fromEntity(com.yushan.backend.entity.Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSlug(category.getSlug());
        dto.setIsActive(category.getIsActive());
        dto.setCreateTime(category.getCreateTime());
        dto.setUpdateTime(category.getUpdateTime());
        return dto;
    }
}
