package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.service.ReportService;
import com.yushan.backend.service.NovelService;
import com.yushan.backend.service.UserService;
import com.yushan.backend.dao.CommentMapper;
import com.yushan.backend.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private NovelService novelService;

    @Mock
    private UserService userService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetailsService.CustomUserDetails userDetails;

    @InjectMocks
    private ReportController reportController;

    private UUID reporterId;
    private Integer novelId;
    private Integer commentId;
    private ReportCreateRequestDTO createRequest;
    private ReportResolutionRequestDTO resolutionRequest;
    private ReportResponseDTO reportResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reporterId = UUID.randomUUID();
        novelId = 1;
        commentId = 1;

        createRequest = new ReportCreateRequestDTO();
        createRequest.setReportType("SPAM");
        createRequest.setReason("This is spam content");

        resolutionRequest = new ReportResolutionRequestDTO();
        resolutionRequest.setAction("RESOLVED");
        resolutionRequest.setAdminNotes("Content removed");

        reportResponse = new ReportResponseDTO();
        reportResponse.setId(1);
        reportResponse.setUuid(UUID.randomUUID());
        reportResponse.setReporterId(reporterId);
        reportResponse.setReportType("SPAM");
        reportResponse.setReason("This is spam content");
        reportResponse.setStatus("IN_REVIEW");
        reportResponse.setContentType("NOVEL");
        reportResponse.setContentId(novelId);
        reportResponse.setCreatedAt(new Date());
        reportResponse.setUpdatedAt(new Date());

        // Setup authentication mock
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(reporterId.toString());
    }

    @Test
    void reportNovel_ShouldReturnSuccessResponse() {
        // Given
        when(reportService.createNovelReport(eq(reporterId), eq(novelId), any(ReportCreateRequestDTO.class)))
                .thenReturn(reportResponse);

        // When
        ApiResponse<ReportResponseDTO> result = reportController.reportNovel(novelId, createRequest, authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Novel reported successfully", result.getMessage());
        assertEquals("SPAM", result.getData().getReportType());
        assertEquals("This is spam content", result.getData().getReason());
        assertEquals("IN_REVIEW", result.getData().getStatus());
        
        verify(reportService).createNovelReport(reporterId, novelId, createRequest);
    }

    @Test
    void reportComment_ShouldReturnSuccessResponse() {
        // Given
        ReportResponseDTO commentReportResponse = new ReportResponseDTO();
        commentReportResponse.setId(2);
        commentReportResponse.setUuid(UUID.randomUUID());
        commentReportResponse.setReporterId(reporterId);
        commentReportResponse.setReportType("HATE_BULLYING");
        commentReportResponse.setReason("Hate speech");
        commentReportResponse.setStatus("IN_REVIEW");
        commentReportResponse.setContentType("COMMENT");
        commentReportResponse.setContentId(commentId);
        commentReportResponse.setCreatedAt(new Date());
        commentReportResponse.setUpdatedAt(new Date());

        when(reportService.createCommentReport(eq(reporterId), eq(commentId), any(ReportCreateRequestDTO.class)))
                .thenReturn(commentReportResponse);

        // When
        ApiResponse<ReportResponseDTO> result = reportController.reportComment(commentId, createRequest, authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Comment reported successfully", result.getMessage());
        assertEquals("HATE_BULLYING", result.getData().getReportType());
        assertEquals("COMMENT", result.getData().getContentType());
        
        verify(reportService).createCommentReport(reporterId, commentId, createRequest);
    }

    @Test
    void getMyReports_ShouldReturnUserReports() {
        // Given
        List<ReportResponseDTO> userReports = List.of(reportResponse);
        when(reportService.getReportsByReporter(reporterId)).thenReturn(userReports);

        // When
        ApiResponse<List<ReportResponseDTO>> result = reportController.getMyReports(authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Reports retrieved successfully", result.getMessage());
        assertEquals(1, result.getData().size());
        assertEquals("SPAM", result.getData().get(0).getReportType());
        
        verify(reportService).getReportsByReporter(reporterId);
    }

    @Test
    void getReportsForAdmin_ShouldReturnPaginatedReports() {
        // Given
        PageResponseDTO<ReportResponseDTO> paginatedReports = PageResponseDTO.of(
                List.of(reportResponse), 1L, 0, 10);
        when(reportService.getReportsForAdmin(any(ReportSearchRequestDTO.class)))
                .thenReturn(paginatedReports);

        // When
        ApiResponse<PageResponseDTO<ReportResponseDTO>> result = reportController.getReportsForAdmin(
                "IN_REVIEW", null, null, "createdAt", "desc", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Reports retrieved successfully", result.getMessage());
        assertEquals(1, result.getData().getContent().size());
        assertEquals(1L, result.getData().getTotalElements());
        assertEquals(0, result.getData().getCurrentPage());
        
        verify(reportService).getReportsForAdmin(any(ReportSearchRequestDTO.class));
    }

    @Test
    void resolveReport_ShouldResolveReportSuccessfully() {
        // Given
        Integer reportId = 1;
        ReportResponseDTO resolvedReport = new ReportResponseDTO();
        resolvedReport.setId(reportId);
        resolvedReport.setStatus("RESOLVED");
        resolvedReport.setAdminNotes("Content removed");
        resolvedReport.setResolvedBy(reporterId);

        when(reportService.resolveReport(eq(reportId), eq(reporterId), any(ReportResolutionRequestDTO.class)))
                .thenReturn(resolvedReport);

        // When
        ApiResponse<ReportResponseDTO> result = reportController.resolveReport(reportId, resolutionRequest, authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Report resolved successfully", result.getMessage());
        assertEquals("RESOLVED", result.getData().getStatus());
        assertEquals("Content removed", result.getData().getAdminNotes());
        
        verify(reportService).resolveReport(reportId, reporterId, resolutionRequest);
    }
}