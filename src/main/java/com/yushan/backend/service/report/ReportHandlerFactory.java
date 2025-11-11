package com.yushan.backend.service.report;

import com.yushan.backend.enums.ReportContentType;
import org.springframework.stereotype.Component;

@Component
public class ReportHandlerFactory {

    private final NovelReportHandler novelReportHandler;
    private final CommentReportHandler commentReportHandler;

    public ReportHandlerFactory(NovelReportHandler novelReportHandler,
                                CommentReportHandler commentReportHandler) {
        this.novelReportHandler = novelReportHandler;
        this.commentReportHandler = commentReportHandler;
    }

    public ReportHandler create(ReportContentType contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("Report content type must not be null");
        }
        switch (contentType) {
            case NOVEL:
                return novelReportHandler;
            case COMMENT:
                return commentReportHandler;
            default:
                throw new IllegalArgumentException("Unsupported report content type: " + contentType);
        }
    }
}


