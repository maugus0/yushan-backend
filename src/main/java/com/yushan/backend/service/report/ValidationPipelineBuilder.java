package com.yushan.backend.service.report;

import org.springframework.stereotype.Component;

@Component
public class ValidationPipelineBuilder {

    private final ContentExistenceHandler contentExistenceHandler;
    private final ReportTypeHandler reportTypeHandler;
    private final DuplicateReportHandler duplicateReportHandler;
    private ValidationHandler head;

    public ValidationPipelineBuilder(ContentExistenceHandler contentExistenceHandler,
                                     ReportTypeHandler reportTypeHandler,
                                     DuplicateReportHandler duplicateReportHandler) {
        this.contentExistenceHandler = contentExistenceHandler;
        this.reportTypeHandler = reportTypeHandler;
        this.duplicateReportHandler = duplicateReportHandler;
        initializeChain();
    }

    private void initializeChain() {
        contentExistenceHandler.setNext(reportTypeHandler)
                .setNext(duplicateReportHandler);
        this.head = contentExistenceHandler;
    }

    public ValidationHandler build() {
        return head;
    }
}


