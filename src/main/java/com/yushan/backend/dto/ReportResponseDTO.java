package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ReportResponseDTO {
    private Integer id;
    private UUID uuid;
    private UUID reporterId;
    private String reporterUsername;
    private String reportType;
    private String reason;
    private String status;
    private String adminNotes;
    private UUID resolvedBy;
    private String resolvedByUsername;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private Date updatedAt;

    // Content info
    private String contentType;
    private Integer contentId;
    
    // Related content details
    private Integer novelId;
    private String novelTitle;
    private Integer commentId;
    private String commentContent;

    public Date getCreatedAt() {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
    }

    public Date getUpdatedAt() {
        return updatedAt != null ? new Date(updatedAt.getTime()) : null;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt != null ? new Date(updatedAt.getTime()) : null;
    }
}
