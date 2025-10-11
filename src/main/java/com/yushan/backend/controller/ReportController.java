package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.service.ReportService;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Report a novel
     */
    @PostMapping("/novel/{novelId}")
    public ApiResponse<ReportResponseDTO> reportNovel(
            @PathVariable Integer novelId,
            @Valid @RequestBody ReportCreateRequestDTO request,
            Authentication authentication) {
        
        UUID reporterId = extractUserId(authentication);
        ReportResponseDTO report = reportService.createNovelReport(reporterId, novelId, request);
        return ApiResponse.success("Novel reported successfully", report);
    }

    /**
     * Report a comment
     */
    @PostMapping("/comment/{commentId}")
    public ApiResponse<ReportResponseDTO> reportComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody ReportCreateRequestDTO request,
            Authentication authentication) {
        
        UUID reporterId = extractUserId(authentication);
        ReportResponseDTO report = reportService.createCommentReport(reporterId, commentId, request);
        return ApiResponse.success("Comment reported successfully", report);
    }

    /**
     * Get user's own reports
     */
    @GetMapping("/my-reports")
    public ApiResponse<List<ReportResponseDTO>> getMyReports(Authentication authentication) {
        UUID reporterId = extractUserId(authentication);
        List<ReportResponseDTO> reports = reportService.getReportsByReporter(reporterId);
        return ApiResponse.success("Reports retrieved successfully", reports);
    }

    /**
     * Get all reports for admin dashboard
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponseDTO<ReportResponseDTO>> getReportsForAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ReportSearchRequestDTO request = new ReportSearchRequestDTO();
        request.setStatus(status);
        request.setReportType(reportType);
        request.setSearch(search);
        request.setSort(sort);
        request.setOrder(order);
        request.setPage(page);
        request.setSize(size);

        PageResponseDTO<ReportResponseDTO> reports = reportService.getReportsForAdmin(request);
        return ApiResponse.success("Reports retrieved successfully", reports);
    }

    /**
     * Get report details by ID (admin only)
     */
    @GetMapping("/admin/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReportResponseDTO> getReportDetails(@PathVariable Integer reportId) {
        ReportResponseDTO report = reportService.getReportById(reportId);
        return ApiResponse.success("Report details retrieved successfully", report);
    }

    /**
     * Resolve a report (admin only)
     */
    @PutMapping("/admin/{reportId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReportResponseDTO> resolveReport(
            @PathVariable Integer reportId,
            @Valid @RequestBody ReportResolutionRequestDTO request,
            Authentication authentication) {
        
        UUID adminId = extractUserId(authentication);
        ReportResponseDTO report = reportService.resolveReport(reportId, adminId, request);
        return ApiResponse.success("Report resolved successfully", report);
    }

    /**
     * Extract user ID from authentication
     */
    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return UUID.fromString(userDetails.getUserId());
        }

        throw new RuntimeException("Invalid authentication");
    }
}
