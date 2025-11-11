package com.yushan.backend.repository;

import com.yushan.backend.dao.ReportMapper;
import com.yushan.backend.entity.Report;
import com.yushan.backend.enums.ReportContentType;
import com.yushan.backend.enums.ReportStatus;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ReportRepository {

    private final ReportMapper reportMapper;

    public ReportRepository(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    public Report save(Report report) {
        reportMapper.insertSelective(report);
        return report;
    }

    public boolean existsActiveReport(UUID reporterId, ReportContentType contentType, Integer contentId) {
        return reportMapper.existsReportByUserAndContent(reporterId, contentType.name(), contentId);
    }

    public Report findById(Integer id) {
        return reportMapper.selectByPrimaryKey(id);
    }

    public void updateResolution(Integer reportId, ReportStatus status, String adminNotes, UUID adminId) {
        reportMapper.updateReportStatus(reportId, status.name(), adminNotes, adminId);
    }
}


