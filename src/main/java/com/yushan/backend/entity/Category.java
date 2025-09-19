package com.yushan.backend.entity;

import java.util.Date;

public class Category {
    private Integer id;

    private String name;

    private String description;

    private String slug;

    private Boolean isActive;

    private Date createTime;

    private Date updateTime;

    public Category(Integer id, String name, String description, String slug, Boolean isActive, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.isActive = isActive;
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public Category() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug == null ? null : slug.trim();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreateTime() {
        return createTime != null ? new Date(createTime.getTime()) : null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
    }

    public Date getUpdateTime() {
        return updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
    }
}