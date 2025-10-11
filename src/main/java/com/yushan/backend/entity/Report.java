package com.yushan.backend.entity;

import java.util.Date;
import java.util.UUID;

public class Report {
    private Integer id;

    private UUID uuid;

    private UUID reporterId;

    private String reportType;

    private String reason;

    private String status;

    private String adminNotes;

    private UUID resolvedBy;

    private Date createdAt;

    private Date updatedAt;

    private String contentType;

    private Integer contentId;

    public Report(Integer id, UUID uuid, UUID reporterId, String reportType, String reason, String status, String adminNotes, UUID resolvedBy, Date createdAt, Date updatedAt, String contentType, Integer contentId) {
        this.id = id;
        this.uuid = uuid;
        this.reporterId = reporterId;
        this.reportType = reportType;
        this.reason = reason;
        this.status = status;
        this.adminNotes = adminNotes;
        this.resolvedBy = resolvedBy;
        this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
        this.updatedAt = updatedAt != null ? new Date(updatedAt.getTime()) : null;
        this.contentType = contentType;
        this.contentId = contentId;
    }

    public Report() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public void setReporterId(UUID reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType == null ? null : reportType.trim();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes == null ? null : adminNotes.trim();
    }

    public UUID getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(UUID resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType == null ? null : contentType.trim();
    }

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }
}
