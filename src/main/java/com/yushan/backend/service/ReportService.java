package com.yushan.backend.service;

import com.yushan.backend.dao.CommentMapper;
import com.yushan.backend.dao.ReportMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Report;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Comment;
import com.yushan.backend.enums.ReportContentType;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.repository.ReportRepository;
import com.yushan.backend.service.report.ReportContext;
import com.yushan.backend.service.report.ReportHandler;
import com.yushan.backend.service.report.ReportHandlerFactory;
import com.yushan.backend.service.report.ValidationHandler;
import com.yushan.backend.service.report.ValidationPipelineBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private NovelService novelService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ReportHandlerFactory reportHandlerFactory;

    @Autowired
    private ValidationPipelineBuilder validationPipelineBuilder;

    @Autowired
    private ReportRepository reportRepository;

    /**
     * Create a report for a novel
     */
    @Transactional
    public ReportResponseDTO createNovelReport(UUID reporterId, Integer novelId, ReportCreateRequestDTO request) {
        return submitReport(ReportContentType.NOVEL, novelId, reporterId, request);
    }

    /**
     * Create a report for a comment
     */
    @Transactional
    public ReportResponseDTO createCommentReport(UUID reporterId, Integer commentId, ReportCreateRequestDTO request) {
        return submitReport(ReportContentType.COMMENT, commentId, reporterId, request);
    }

    /**
     * Get reports for admin dashboard with pagination and filtering
     */
    public PageResponseDTO<ReportResponseDTO> getReportsForAdmin(ReportSearchRequestDTO request) {
        List<Report> reports = reportMapper.selectReportsWithPagination(request);
        long totalElements = reportMapper.countReports(request);

        List<ReportResponseDTO> reportDTOs = reports.stream()
                .map(this::toReportResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.of(reportDTOs, totalElements, request.getPage(), request.getSize());
    }

    /**
     * Get report details by ID
     */
    public ReportResponseDTO getReportById(Integer reportId) {
        Report report = reportMapper.selectByPrimaryKey(reportId);
        if (report == null) {
            throw new ResourceNotFoundException("Report not found");
        }

        return toReportResponseDTO(report, null, null);
    }

    /**
     * Resolve a report (mark as resolved or dismissed)
     */
    @Transactional
    public ReportResponseDTO resolveReport(Integer reportId, UUID adminId, ReportResolutionRequestDTO request) {
        Report report = reportMapper.selectByPrimaryKey(reportId);
        if (report == null) {
            throw new ResourceNotFoundException("Report not found");
        }

        // Validate action
        if (!"RESOLVED".equals(request.getAction()) && !"DISMISSED".equals(request.getAction())) {
            throw new ValidationException("Invalid action. Must be RESOLVED or DISMISSED");
        }

        // Update report status
        reportMapper.updateReportStatus(
                reportId,
                request.getAction(),
                request.getAdminNotes(),
                adminId
        );

        // Get updated report
        Report updatedReport = reportMapper.selectByPrimaryKey(reportId);
        return toReportResponseDTO(updatedReport, null, null);
    }

    /**
     * Get reports by reporter ID
     */
    public List<ReportResponseDTO> getReportsByReporter(UUID reporterId) {
        List<Report> reports = reportMapper.selectReportsByReporterId(reporterId);
        return reports.stream()
                .map(this::toReportResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Report entity to ReportResponseDTO
     */
    private ReportResponseDTO toReportResponseDTO(Report report, Novel novel, Comment comment) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(report.getId());
        dto.setUuid(report.getUuid());
        dto.setReporterId(report.getReporterId());
        dto.setReportType(report.getReportType());
        dto.setReason(report.getReason());
        dto.setStatus(report.getStatus());
        dto.setAdminNotes(report.getAdminNotes());
        dto.setResolvedBy(report.getResolvedBy());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());
        dto.setContentType(report.getContentType());
        dto.setContentId(report.getContentId());

        // Get reporter username
        try {
            String username = userService.getUsernameById(report.getReporterId());
            dto.setReporterUsername(username);
        } catch (Exception e) {
            // User might be deleted, set username as null
            dto.setReporterUsername(null);
        }

        // Get resolved by username
        if (report.getResolvedBy() != null) {
            try {
                String username = userService.getUsernameById(report.getResolvedBy());
                dto.setResolvedByUsername(username);
            } catch (Exception e) {
                // Admin might be deleted, set username as null
                dto.setResolvedByUsername(null);
            }
        }

        // Set related content info
        if (novel != null) {
            dto.setNovelId(novel.getId());
            dto.setNovelTitle(novel.getTitle());
        } else if ("NOVEL".equals(report.getContentType())) {
            // If novel is not passed but content type is NOVEL, fetch it
            try {
                Novel relatedNovel = novelService.getNovelEntity(report.getContentId());
                dto.setNovelId(relatedNovel.getId());
                dto.setNovelTitle(relatedNovel.getTitle());
            } catch (ResourceNotFoundException e) {
                // Novel might be deleted
                dto.setNovelId(report.getContentId());
                dto.setNovelTitle("Deleted Novel");
            }
        }

        if (comment != null) {
            dto.setCommentId(comment.getId());
            dto.setCommentContent(comment.getContent());
        } else if ("COMMENT".equals(report.getContentType())) {
            // If comment is not passed but content type is COMMENT, fetch it
            Comment relatedComment = commentMapper.selectByPrimaryKey(report.getContentId());
            if (relatedComment != null) {
                dto.setCommentId(relatedComment.getId());
                dto.setCommentContent(relatedComment.getContent());
            } else {
                dto.setCommentId(report.getContentId());
                dto.setCommentContent("Deleted Comment");
            }
        }

        return dto;
    }

    /**
     * Convert Report entity to ReportResponseDTO (without related content)
     */
    private ReportResponseDTO toReportResponseDTO(Report report) {
        return toReportResponseDTO(report, null, null);
    }

    private ReportResponseDTO submitReport(ReportContentType contentType,
                                           Integer contentId,
                                           UUID reporterId,
                                           ReportCreateRequestDTO request) {
        ReportContext context = new ReportContext(contentType, contentId, reporterId, request);
        ValidationHandler pipeline = validationPipelineBuilder.build();
        pipeline.handle(context);

        ReportHandler handler = reportHandlerFactory.create(contentType);
        Report report = handler.buildReport(context);

        reportRepository.save(report);

        ReportResponseDTO response = toReportResponseDTO(report, context.getNovel(), context.getComment());
        handler.enrichResponse(context, report, response);
        return response;
    }
}
