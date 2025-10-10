package com.yushan.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public class ChapterPublishRequestDTO {
    @NotNull(message = "Chapter UUID is required")
    private UUID uuid;

    @NotNull(message = "Publish status is required")
    private Boolean isValid;

    private Date publishTime;

    // Constructors
    public ChapterPublishRequestDTO() {}

    public ChapterPublishRequestDTO(UUID uuid, Boolean isValid, Date publishTime) {
        this.uuid = uuid;
        this.isValid = isValid;
        this.publishTime = publishTime != null ? (Date) publishTime.clone() : null;
    }

    // Getters and Setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }

    public Date getPublishTime() { 
        return publishTime != null ? (Date) publishTime.clone() : null; 
    }
    public void setPublishTime(Date publishTime) { 
        this.publishTime = publishTime != null ? (Date) publishTime.clone() : null; 
    }
}