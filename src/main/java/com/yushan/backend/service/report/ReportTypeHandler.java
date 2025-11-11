package com.yushan.backend.service.report;

import com.yushan.backend.enums.ReportType;
import com.yushan.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ReportTypeHandler extends ValidationHandler {

    @Override
    protected void doHandle(ReportContext context) {
        ReportType reportType = ReportType.fromString(context.getRequest().getReportType());
        if (reportType == null) {
            throw new ValidationException("Invalid report type");
        }
        context.setReportType(reportType);
    }
}


