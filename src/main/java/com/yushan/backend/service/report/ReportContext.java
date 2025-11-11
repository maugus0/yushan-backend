package com.yushan.backend.service.report;

import com.yushan.backend.dto.ReportCreateRequestDTO;
import com.yushan.backend.entity.Comment;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.enums.ReportContentType;
import com.yushan.backend.enums.ReportType;

import java.util.UUID;

public class ReportContext {
    private final ReportContentType contentType;
    private final Integer contentId;
    private final UUID reporterId;
    private final ReportCreateRequestDTO request;

    private Novel novel;
    private Comment comment;
    private ReportType reportType;

    public ReportContext(ReportContentType contentType, Integer contentId, UUID reporterId,
                         ReportCreateRequestDTO request) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.reporterId = reporterId;
        this.request = request;
    }

    public ReportContentType getContentType() {
        return contentType;
    }

    public Integer getContentId() {
        return contentId;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public ReportCreateRequestDTO getRequest() {
        return request;
    }

    public Novel getNovel() {
        return novel;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}


