package com.yushan.backend.service.report;

import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.repository.ReportRepository;
import org.springframework.stereotype.Component;

@Component
public class DuplicateReportHandler extends ValidationHandler {

    private final ReportRepository reportRepository;

    public DuplicateReportHandler(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    protected void doHandle(ReportContext context) {
        boolean exists = reportRepository.existsActiveReport(
                context.getReporterId(),
                context.getContentType(),
                context.getContentId()
        );
        if (exists) {
            throw new ValidationException(buildMessage(context));
        }
    }

    private String buildMessage(ReportContext context) {
        if (context.getContentType() == null) {
            return "You have already reported this content";
        }
        switch (context.getContentType()) {
            case NOVEL:
                return "You have already reported this novel";
            case COMMENT:
                return "You have already reported this comment";
            default:
                return "You have already reported this content";
        }
    }
}


