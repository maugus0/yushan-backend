package com.yushan.backend.service.report;

import com.yushan.backend.dto.ReportResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Report;
import com.yushan.backend.enums.ReportContentType;
import com.yushan.backend.enums.ReportStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class NovelReportHandler implements ReportHandler {

    @Override
    public Report buildReport(ReportContext context) {
        Report report = new Report();
        report.setUuid(UUID.randomUUID());
        report.setReporterId(context.getReporterId());
        report.setReportType(context.getReportType().name());
        report.setReason(context.getRequest().getReason());
        report.setStatus(ReportStatus.IN_REVIEW.name());
        report.setContentType(ReportContentType.NOVEL.name());
        report.setContentId(context.getContentId());
        Date now = new Date();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        return report;
    }

    @Override
    public void enrichResponse(ReportContext context, Report report, ReportResponseDTO dto) {
        Novel novel = context.getNovel();
        if (novel != null) {
            dto.setNovelId(novel.getId());
            dto.setNovelTitle(novel.getTitle());
        }
    }
}


