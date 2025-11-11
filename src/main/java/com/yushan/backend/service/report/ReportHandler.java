package com.yushan.backend.service.report;

import com.yushan.backend.dto.ReportResponseDTO;
import com.yushan.backend.entity.Report;

public interface ReportHandler {

    Report buildReport(ReportContext context);

    void enrichResponse(ReportContext context, Report report, ReportResponseDTO dto);
}


