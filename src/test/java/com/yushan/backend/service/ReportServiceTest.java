package com.yushan.backend.service;

import com.yushan.backend.dao.ReportMapper;
import com.yushan.backend.dao.CommentMapper;
import com.yushan.backend.dto.ReportCreateRequestDTO;
import com.yushan.backend.dto.ReportResponseDTO;
import com.yushan.backend.dto.ReportResolutionRequestDTO;
import com.yushan.backend.entity.Report;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Comment;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private NovelService novelService;

    @Mock
    private UserService userService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ReportService reportService;

    private UUID reporterId;
    private Integer novelId;
    private Integer commentId;
    private ReportCreateRequestDTO requestDTO;
    private Novel novel;
    private Comment comment;
    private Report report;

    @BeforeEach
    void setUp() {
        reporterId = UUID.randomUUID();
        novelId = 1;
        commentId = 1;
        
        requestDTO = new ReportCreateRequestDTO();
        requestDTO.setReportType("SPAM");
        requestDTO.setReason("This is spam content");

        novel = new Novel();
        novel.setId(novelId);
        novel.setTitle("Test Novel");

        comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Test comment");

        report = new Report();
        report.setId(1);
        report.setUuid(UUID.randomUUID());
        report.setReporterId(reporterId);
        report.setReportType("SPAM");
        report.setReason("This is spam content");
        report.setStatus("IN_REVIEW");
        report.setContentType("NOVEL");
        report.setContentId(novelId);
        report.setCreatedAt(new Date());
        report.setUpdatedAt(new Date());
    }

    @Test
    void createNovelReport_ShouldCreateReportSuccessfully() {
        // Given
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(reportMapper.existsReportByUserAndContent(reporterId, "NOVEL", novelId)).thenReturn(false);
        when(reportMapper.insertSelective(any(Report.class))).thenReturn(1);
        when(userService.getUsernameById(reporterId)).thenReturn("testuser");

        // When
        ReportResponseDTO result = reportService.createNovelReport(reporterId, novelId, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("SPAM", result.getReportType());
        assertEquals("This is spam content", result.getReason());
        assertEquals("IN_REVIEW", result.getStatus());
        assertEquals("NOVEL", result.getContentType());
        assertEquals(novelId, result.getContentId());
        assertEquals("testuser", result.getReporterUsername());

        verify(novelService).getNovelEntity(novelId);
        verify(reportMapper).existsReportByUserAndContent(reporterId, "NOVEL", novelId);
        verify(reportMapper).insertSelective(any(Report.class));
    }

    @Test
    void createNovelReport_ShouldThrowExceptionWhenNovelNotFound() {
        // Given
        when(novelService.getNovelEntity(novelId)).thenThrow(new ResourceNotFoundException("Novel not found"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> 
            reportService.createNovelReport(reporterId, novelId, requestDTO));
        
        verify(novelService).getNovelEntity(novelId);
        verify(reportMapper, never()).insertSelective(any(Report.class));
    }

    @Test
    void createNovelReport_ShouldThrowExceptionWhenAlreadyReported() {
        // Given
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(reportMapper.existsReportByUserAndContent(reporterId, "NOVEL", novelId)).thenReturn(true);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> 
            reportService.createNovelReport(reporterId, novelId, requestDTO));
        
        assertEquals("You have already reported this novel", exception.getMessage());
        verify(reportMapper, never()).insertSelective(any(Report.class));
    }

    @Test
    void resolveReport_ShouldResolveReportSuccessfully() {
        // Given
        Integer reportId = 1;
        UUID adminId = UUID.randomUUID();
        ReportResolutionRequestDTO resolutionRequest = new ReportResolutionRequestDTO();
        resolutionRequest.setAction("RESOLVED");
        resolutionRequest.setAdminNotes("Content removed");

        when(reportMapper.selectByPrimaryKey(reportId)).thenReturn(report);
        when(reportMapper.updateReportStatus(reportId, "RESOLVED", "Content removed", adminId)).thenReturn(1);
        when(userService.getUsernameById(adminId)).thenReturn("adminuser");
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);

        // When
        ReportResponseDTO result = reportService.resolveReport(reportId, adminId, resolutionRequest);

        // Then
        assertNotNull(result);
        verify(reportMapper).updateReportStatus(reportId, "RESOLVED", "Content removed", adminId);
    }

    @Test
    void resolveReport_ShouldThrowExceptionWhenInvalidAction() {
        // Given
        Integer reportId = 1;
        UUID adminId = UUID.randomUUID();
        ReportResolutionRequestDTO resolutionRequest = new ReportResolutionRequestDTO();
        resolutionRequest.setAction("INVALID_ACTION");
        resolutionRequest.setAdminNotes("Test notes");

        when(reportMapper.selectByPrimaryKey(reportId)).thenReturn(report);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> 
            reportService.resolveReport(reportId, adminId, resolutionRequest));
        
        assertEquals("Invalid action. Must be RESOLVED or DISMISSED", exception.getMessage());
        verify(reportMapper, never()).updateReportStatus(anyInt(), anyString(), anyString(), any(UUID.class));
    }
}
