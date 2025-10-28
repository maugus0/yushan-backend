package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Report DTO Tests")
class ReportDTOTest {

    @Test
    @DisplayName("Test ReportCreateRequestDTO")
    void testReportCreateRequestDTO() {
        ReportCreateRequestDTO dto = new ReportCreateRequestDTO();
        dto.setReportType("COMMENT");
        dto.setReason("Spam");
        
        assertEquals("COMMENT", dto.getReportType());
        assertEquals("Spam", dto.getReason());
    }

    @Test
    @DisplayName("Test ReportResponseDTO")
    void testReportResponseDTO() {
        UUID reporterId = UUID.randomUUID();
        UUID resolvedBy = UUID.randomUUID();
        UUID uuid = UUID.randomUUID();
        Date now = new Date();
        
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(1);
        dto.setUuid(uuid);
        dto.setReportType("COMMENT");
        dto.setReason("Harassment");
        dto.setStatus("PENDING");
        dto.setAdminNotes("Admin notes here");
        dto.setReporterId(reporterId);
        dto.setReporterUsername("reporter");
        dto.setResolvedBy(resolvedBy);
        dto.setResolvedByUsername("admin");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setContentType("COMMENT");
        dto.setContentId(123);
        dto.setNovelId(456);
        dto.setNovelTitle("Test Novel");
        dto.setCommentId(789);
        dto.setCommentContent("Offensive comment");
        
        assertEquals(1, dto.getId());
        assertEquals(uuid, dto.getUuid());
        assertEquals("COMMENT", dto.getReportType());
        assertEquals("Harassment", dto.getReason());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("Admin notes here", dto.getAdminNotes());
        assertEquals(reporterId, dto.getReporterId());
        assertEquals("reporter", dto.getReporterUsername());
        assertEquals(resolvedBy, dto.getResolvedBy());
        assertEquals("admin", dto.getResolvedByUsername());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
        assertEquals("COMMENT", dto.getContentType());
        assertEquals(123, dto.getContentId());
        assertEquals(456, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals(789, dto.getCommentId());
        assertEquals("Offensive comment", dto.getCommentContent());
        
        // Test defensive copying for Date fields
        Date testDate = new Date();
        dto.setCreatedAt(testDate);
        Date retrieved = dto.getCreatedAt();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        dto.setUpdatedAt(testDate);
        retrieved = dto.getUpdatedAt();
        assertNotSame(testDate, retrieved);
        
        // Test null Date values
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        
        // Test different status values
        dto.setStatus("RESOLVED");
        assertEquals("RESOLVED", dto.getStatus());
        dto.setStatus("DISMISSED");
        assertEquals("DISMISSED", dto.getStatus());
        
        // Test different report types
        dto.setReportType("NOVEL");
        assertEquals("NOVEL", dto.getReportType());
        dto.setReportType("REVIEW");
        assertEquals("REVIEW", dto.getReportType());
        
        // Test with null values
        dto.setAdminNotes(null);
        dto.setResolvedBy(null);
        dto.setResolvedByUsername(null);
        assertNull(dto.getAdminNotes());
        assertNull(dto.getResolvedBy());
        assertNull(dto.getResolvedByUsername());
        
        // Test all remaining fields with different values
        UUID uuid2 = UUID.randomUUID();
        UUID reporterId2 = UUID.randomUUID();
        UUID resolvedBy2 = UUID.randomUUID();
        
        dto.setId(999);
        dto.setUuid(uuid2);
        dto.setReporterId(reporterId2);
        dto.setReporterUsername("differentReporter");
        dto.setResolvedBy(resolvedBy2);
        dto.setResolvedByUsername("differentAdmin");
        dto.setContentType("NOVEL");
        dto.setContentId(999);
        dto.setNovelId(888);
        dto.setNovelTitle("Reported Novel");
        dto.setCommentId(777);
        dto.setCommentContent("Reported Comment");
        
        assertEquals(999, dto.getId());
        assertEquals(uuid2, dto.getUuid());
        assertEquals(reporterId2, dto.getReporterId());
        assertEquals("differentReporter", dto.getReporterUsername());
        assertEquals(resolvedBy2, dto.getResolvedBy());
        assertEquals("differentAdmin", dto.getResolvedByUsername());
        assertEquals("NOVEL", dto.getContentType());
        assertEquals(999, dto.getContentId());
        assertEquals(888, dto.getNovelId());
        assertEquals("Reported Novel", dto.getNovelTitle());
        assertEquals(777, dto.getCommentId());
        assertEquals("Reported Comment", dto.getCommentContent());
        
        // Test getCreatedAt with null check branch
        dto.setCreatedAt(null);
        assertNull(dto.getCreatedAt());
        
        Date testCreatedAt = new Date();
        dto.setCreatedAt(testCreatedAt);
        Date retrievedCreatedAt = dto.getCreatedAt();
        assertNotNull(retrievedCreatedAt);
        assertNotSame(testCreatedAt, retrievedCreatedAt);
        
        // Test getUpdatedAt with null check branch
        dto.setUpdatedAt(null);
        assertNull(dto.getUpdatedAt());
        
        Date testUpdatedAt = new Date();
        dto.setUpdatedAt(testUpdatedAt);
        Date retrievedUpdatedAt = dto.getUpdatedAt();
        assertNotNull(retrievedUpdatedAt);
        assertNotSame(testUpdatedAt, retrievedUpdatedAt);
        
        // Test setCreatedAt with null parameter branch
        dto.setCreatedAt(null);
        assertNull(dto.getCreatedAt());
        
        // Test setUpdatedAt with null parameter branch
        dto.setUpdatedAt(null);
        assertNull(dto.getUpdatedAt());
        
        // Test setCreatedAt with non-null parameter branch
        Date newCreatedAt = new Date();
        dto.setCreatedAt(newCreatedAt);
        Date retrievedCreated = dto.getCreatedAt();
        assertNotNull(retrievedCreated);
        assertNotSame(newCreatedAt, retrievedCreated);
        
        // Test setUpdatedAt with non-null parameter branch
        Date newUpdatedAt = new Date();
        dto.setUpdatedAt(newUpdatedAt);
        Date retrievedUpdated = dto.getUpdatedAt();
        assertNotNull(retrievedUpdated);
        assertNotSame(newUpdatedAt, retrievedUpdated);
        
        // Test defensive copying - modify original date should not affect retrieved
        testCreatedAt.setTime(0);
        assertNotEquals(0, retrievedCreatedAt.getTime());
        
        testUpdatedAt.setTime(0);
        assertNotEquals(0, retrievedUpdatedAt.getTime());
    }

    @Test
    @DisplayName("Test ReportResponseDTO with comment")
    void testReportResponseDTOWithComment() {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(2);
        dto.setReportType("COMMENT");
        dto.setCommentId(789);
        dto.setCommentContent("Offensive comment text");
        
        assertEquals(2, dto.getId());
        assertEquals("COMMENT", dto.getReportType());
        assertEquals(789, dto.getCommentId());
        assertEquals("Offensive comment text", dto.getCommentContent());
    }

    @Test
    @DisplayName("Test ReportSearchRequestDTO")
    void testReportSearchRequestDTO() {
        ReportSearchRequestDTO dto = new ReportSearchRequestDTO();
        dto.setStatus("PENDING");
        dto.setReportType("COMMENT");
        dto.setSearch("test");
        dto.setSort("createdAt");
        dto.setOrder("desc");
        dto.setPage(1);
        dto.setSize(20);
        
        assertEquals("PENDING", dto.getStatus());
        assertEquals("COMMENT", dto.getReportType());
        assertEquals("test", dto.getSearch());
        assertEquals("createdAt", dto.getSort());
        assertEquals("desc", dto.getOrder());
        assertEquals(1, dto.getPage());
        assertEquals(20, dto.getSize());
        
        // Test default values
        ReportSearchRequestDTO dto2 = new ReportSearchRequestDTO();
        assertEquals("createdAt", dto2.getSort());
        assertEquals("desc", dto2.getOrder());
        assertEquals(0, dto2.getPage());
        assertEquals(10, dto2.getSize());
    }

    @Test
    @DisplayName("Test ReportResolutionRequestDTO")
    void testReportResolutionRequestDTO() {
        ReportResolutionRequestDTO dto = new ReportResolutionRequestDTO();
        dto.setAction("RESOLVED");
        dto.setAdminNotes("User warning issued");
        
        assertEquals("RESOLVED", dto.getAction());
        assertEquals("User warning issued", dto.getAdminNotes());
        
        dto.setAction("DISMISSED");
        assertEquals("DISMISSED", dto.getAction());
        
        // Test equals, hashCode, and canEqual methods for ReportResponseDTO
        UUID uuid2 = UUID.randomUUID();
        
        ReportResponseDTO dto3 = new ReportResponseDTO();
        dto3.setId(1);
        dto3.setUuid(uuid2);
        dto3.setReportType("COMMENT");
        dto3.setStatus("PENDING");
        
        ReportResponseDTO dto4 = new ReportResponseDTO();
        dto4.setId(1);
        dto4.setUuid(uuid2);
        dto4.setReportType("COMMENT");
        dto4.setStatus("PENDING");
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
        
        // Test equals, hashCode, and canEqual methods for ReportCreateRequestDTO
        ReportCreateRequestDTO dto5 = new ReportCreateRequestDTO();
        dto5.setReportType("COMMENT");
        dto5.setReason("Spam");
        
        ReportCreateRequestDTO dto6 = new ReportCreateRequestDTO();
        dto6.setReportType("COMMENT");
        dto6.setReason("Spam");
        
        assertEquals(dto5, dto6);
        assertEquals(dto5.hashCode(), dto6.hashCode());
        assertNotEquals(dto5, null);
        assertEquals(dto5, dto5);
        assertTrue(dto5.canEqual(dto6));
        
        // Test equals, hashCode, and canEqual methods for ReportResolutionRequestDTO
        ReportResolutionRequestDTO dto7 = new ReportResolutionRequestDTO();
        dto7.setAction("RESOLVED");
        dto7.setAdminNotes("Resolved");
        
        ReportResolutionRequestDTO dto8 = new ReportResolutionRequestDTO();
        dto8.setAction("RESOLVED");
        dto8.setAdminNotes("Resolved");
        
        assertEquals(dto7, dto8);
        assertEquals(dto7.hashCode(), dto8.hashCode());
        assertNotEquals(dto7, null);
        assertEquals(dto7, dto7);
        assertTrue(dto7.canEqual(dto8));
        
        // Test equals, hashCode, and canEqual methods for ReportSearchRequestDTO
        ReportSearchRequestDTO dto9 = new ReportSearchRequestDTO();
        dto9.setStatus("PENDING");
        dto9.setPage(0);
        dto9.setSize(10);
        
        ReportSearchRequestDTO dto10 = new ReportSearchRequestDTO();
        dto10.setStatus("PENDING");
        dto10.setPage(0);
        dto10.setSize(10);
        
        assertEquals(dto9, dto10);
        assertEquals(dto9.hashCode(), dto10.hashCode());
        assertNotEquals(dto9, null);
        assertEquals(dto9, dto9);
        assertTrue(dto9.canEqual(dto10));
    }
    
    @Test
    @DisplayName("Test ReportResponseDTO equals, hashCode, canEqual, toString")
    void testReportResponseDTOEqualsHashCodeToString() {
        UUID reporterId = UUID.randomUUID();
        UUID uuid = UUID.randomUUID();
        Date createdAt = new Date();
        Date updatedAt = new Date();
        
        ReportResponseDTO dto1 = new ReportResponseDTO();
        dto1.setId(1);
        dto1.setUuid(uuid);
        dto1.setReportType("COMMENT");
        dto1.setReason("Spam");
        dto1.setStatus("PENDING");
        dto1.setReporterId(reporterId);
        dto1.setCreatedAt(createdAt);
        dto1.setUpdatedAt(updatedAt);
        
        ReportResponseDTO dto2 = new ReportResponseDTO();
        dto2.setId(1);
        dto2.setUuid(uuid);
        dto2.setReportType("COMMENT");
        dto2.setReason("Spam");
        dto2.setStatus("PENDING");
        dto2.setReporterId(reporterId);
        dto2.setCreatedAt(createdAt);
        dto2.setUpdatedAt(updatedAt);
        
        ReportResponseDTO dto3 = new ReportResponseDTO();
        dto3.setId(2);
        dto3.setReportType("NOVEL");
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        assertTrue(dto1.canEqual(dto2));
        assertTrue(dto1.canEqual(dto3));
        assertFalse(dto1.canEqual(null));
        
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ReportResponseDTO"));
    }
}

