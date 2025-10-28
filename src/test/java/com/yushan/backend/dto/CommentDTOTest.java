package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Comment DTO Tests")
class CommentDTOTest {

    @Test
    @DisplayName("Test CommentCreateRequestDTO - No args constructor")
    void testCommentCreateRequestDTONoArgsConstructor() {
        CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getChapterId());
        assertNull(dto.getContent());
        assertEquals(false, dto.getIsSpoiler()); // Default value
    }
    
    @Test
    @DisplayName("Test CommentCreateRequestDTO - Getter and Setter")
    void testCommentCreateRequestDTOGetterSetter() {
        CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
        
        // Test chapterId setter and getter
        dto.setChapterId(1);
        assertEquals(1, dto.getChapterId());
        
        dto.setChapterId(100);
        assertEquals(100, dto.getChapterId());
        
        // Test content setter and getter
        dto.setContent("Great chapter!");
        assertEquals("Great chapter!", dto.getContent());
        
        dto.setContent("Another comment");
        assertEquals("Another comment", dto.getContent());
        
        // Test isSpoiler setter and getter
        dto.setIsSpoiler(false);
        assertFalse(dto.getIsSpoiler());
        
        dto.setIsSpoiler(true);
        assertTrue(dto.getIsSpoiler());
        
        dto.setIsSpoiler(null);
        assertNull(dto.getIsSpoiler());
        
        // Test with different values
        dto.setChapterId(999);
        dto.setContent("Long comment with many words");
        dto.setIsSpoiler(false);
        assertEquals(999, dto.getChapterId());
        assertEquals("Long comment with many words", dto.getContent());
        assertFalse(dto.getIsSpoiler());
    }
    
    @Test
    @DisplayName("Test CommentCreateRequestDTO - toString")
    void testCommentCreateRequestDTOToString() {
        CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
        dto.setChapterId(1);
        dto.setContent("Test comment");
        dto.setIsSpoiler(false);
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentCreateRequestDTO"));
        assertTrue(toString.contains("chapterId"));
        assertTrue(toString.contains("content"));
    }

    @Test
    @DisplayName("Test CommentCreateRequestDTO with spoiler")
    void testCommentCreateRequestDTOWithSpoiler() {
        CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
        dto.setChapterId(5);
        dto.setContent("Spoiler alert!");
        dto.setIsSpoiler(true);
        
        assertEquals(5, dto.getChapterId());
        assertEquals("Spoiler alert!", dto.getContent());
        assertTrue(dto.getIsSpoiler());
    }

    @Test
    @DisplayName("Test CommentUpdateRequestDTO - Getter and Setter")
    void testCommentUpdateRequestDTOGetterSetter() {
        CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
        
        // Test content setter and getter
        dto.setContent("Updated comment");
        assertEquals("Updated comment", dto.getContent());
        
        dto.setContent("Another updated comment");
        assertEquals("Another updated comment", dto.getContent());
        
        // Test isSpoiler setter and getter
        dto.setIsSpoiler(false);
        assertFalse(dto.getIsSpoiler());
        
        dto.setIsSpoiler(true);
        assertTrue(dto.getIsSpoiler());
        
        dto.setIsSpoiler(null);
        assertNull(dto.getIsSpoiler());
        
        // Test with null values
        dto.setContent(null);
        dto.setIsSpoiler(null);
        assertNull(dto.getContent());
        assertNull(dto.getIsSpoiler());
        
        // Test with empty string
        dto.setContent("");
        assertEquals("", dto.getContent());
        
        // Test with long content (max 2000)
        String longContent = "A".repeat(2000);
        dto.setContent(longContent);
        assertEquals(2000, dto.getContent().length());
    }
    
    @Test
    @DisplayName("Test CommentUpdateRequestDTO - toString")
    void testCommentUpdateRequestDTOToString() {
        CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
        dto.setContent("Test content");
        dto.setIsSpoiler(false);
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentUpdateRequestDTO"));
    }

    @Test
    @DisplayName("Test CommentBatchDeleteRequestDTO")
    void testCommentBatchDeleteRequestDTO() {
        CommentBatchDeleteRequestDTO dto = new CommentBatchDeleteRequestDTO();
        java.util.List<Integer> ids = new java.util.ArrayList<>(Arrays.asList(1, 2, 3));
        dto.setCommentIds(ids);
        dto.setReason("Spam");
        
        assertEquals(3, dto.getCommentIds().size());
        assertEquals("Spam", dto.getReason());
        
        // Test defensive copying
        ids.add(4);
        assertEquals(3, dto.getCommentIds().size());
        
        java.util.List<Integer> retrieved = dto.getCommentIds();
        assertNotSame(ids, retrieved);
        
        // Test with null reason
        dto.setReason(null);
        assertNull(dto.getReason());
        
        // Test with null ids (should handle gracefully)
        dto.setCommentIds(null);
        assertNull(dto.getCommentIds());
        
        // Test getCommentIds with null check branch
        CommentBatchDeleteRequestDTO dto2 = new CommentBatchDeleteRequestDTO();
        assertNull(dto2.getCommentIds());
        
        java.util.List<Integer> ids2 = new java.util.ArrayList<>(Arrays.asList(5, 6, 7));
        dto2.setCommentIds(ids2);
        java.util.List<Integer> retrieved2 = dto2.getCommentIds();
        assertNotNull(retrieved2);
        assertNotSame(ids2, retrieved2);
        
        // Test setCommentIds with null parameter branch
        dto2.setCommentIds(null);
        assertNull(dto2.getCommentIds());
        
        // Test setCommentIds with non-null parameter branch
        java.util.List<Integer> ids3 = new java.util.ArrayList<>(Arrays.asList(8, 9));
        dto2.setCommentIds(ids3);
        java.util.List<Integer> retrieved3 = dto2.getCommentIds();
        assertNotNull(retrieved3);
        assertNotSame(ids3, retrieved3);
        
        // Test equals, hashCode, and canEqual methods
        CommentBatchDeleteRequestDTO dto6 = new CommentBatchDeleteRequestDTO();
        dto6.setReason("Test");
        java.util.List<Integer> ids6 = new java.util.ArrayList<>(Arrays.asList(1, 2));
        dto6.setCommentIds(ids6);
        
        CommentBatchDeleteRequestDTO dto7 = new CommentBatchDeleteRequestDTO();
        dto7.setReason("Test");
        java.util.List<Integer> ids7 = new java.util.ArrayList<>(Arrays.asList(1, 2));
        dto7.setCommentIds(ids7);
        
        // Test equals - same values
        assertEquals(dto6, dto7);
        assertEquals(dto6.hashCode(), dto7.hashCode());
        
        // Test equals - different values
        dto7.setReason("Different");
        assertNotEquals(dto6, dto7);
        
        // Test equals - null
        assertNotEquals(dto6, null);
        
        // Test equals - same object
        assertEquals(dto6, dto6);
        
        // Test canEqual
        assertTrue(dto6.canEqual(dto7));
    }

    @Test
    @DisplayName("Test CommentBatchDeleteRequestDTO without reason")
    void testCommentBatchDeleteRequestDTOWithoutReason() {
        CommentBatchDeleteRequestDTO dto = new CommentBatchDeleteRequestDTO();
        java.util.List<Integer> ids = new java.util.ArrayList<>(Arrays.asList(10, 20));
        dto.setCommentIds(ids);
        
        assertEquals(2, dto.getCommentIds().size());
        assertNull(dto.getReason());
        
        // Test defensive copying
        ids.add(30);
        assertEquals(2, dto.getCommentIds().size());
    }

    @Test
    @DisplayName("Test CommentSearchRequestDTO - equals, hashCode, canEqual")
    void testCommentSearchRequestDTOEqualsHashCode() {
        CommentSearchRequestDTO dto1 = new CommentSearchRequestDTO();
        dto1.setChapterId(1);
        dto1.setNovelId(10);
        dto1.setUserId(UUID.randomUUID());
        dto1.setIsSpoiler(true);
        dto1.setSearch("test");
        dto1.setSort("createTime");
        dto1.setOrder("desc");
        dto1.setPage(0);
        dto1.setSize(20);
        
        CommentSearchRequestDTO dto2 = new CommentSearchRequestDTO();
        dto2.setChapterId(1);
        dto2.setNovelId(10);
        dto2.setUserId(dto1.getUserId());
        dto2.setIsSpoiler(true);
        dto2.setSearch("test");
        dto2.setSort("createTime");
        dto2.setOrder("desc");
        dto2.setPage(0);
        dto2.setSize(20);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        assertTrue(dto1.canEqual(dto2));
        
        // Test with different values
        dto2.setChapterId(2);
        assertNotEquals(dto1, dto2);
        
        dto2.setChapterId(1);
        dto2.setIsSpoiler(false);
        assertNotEquals(dto1, dto2);
        
        // Test toString
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentSearchRequestDTO"));
    }

    @Test
    @DisplayName("Test CommentSearchRequestDTO")
    void testCommentSearchRequestDTO() {
        CommentSearchRequestDTO dto = new CommentSearchRequestDTO();
        dto.setChapterId(1);
        UUID userId = UUID.randomUUID();
        dto.setUserId(userId);
        dto.setIsSpoiler(false);
        dto.setPage(0);
        dto.setSize(20);
        dto.setSort("createTime");
        dto.setOrder("desc");
        
        assertEquals(1, dto.getChapterId());
        assertEquals(userId, dto.getUserId());
        assertFalse(dto.getIsSpoiler());
        assertEquals(0, dto.getPage());
        assertEquals(20, dto.getSize());
        assertEquals("createTime", dto.getSort());
        assertEquals("desc", dto.getOrder());
        
        // Test default values
        CommentSearchRequestDTO dto2 = new CommentSearchRequestDTO();
        assertEquals("createTime", dto2.getSort());
        assertEquals("desc", dto2.getOrder());
        assertEquals(0, dto2.getPage());
        assertEquals(20, dto2.getSize());
        
        // Test builder with all fields
        UUID userId2 = UUID.randomUUID();
        CommentSearchRequestDTO dto3 = CommentSearchRequestDTO.builder()
            .chapterId(5)
            .novelId(200)
            .userId(userId2)
            .isSpoiler(true)
            .search("test search")
            .sort("likeCnt")
            .order("asc")
            .page(2)
            .size(50)
            .build();
        assertEquals(5, dto3.getChapterId());
        assertEquals(200, dto3.getNovelId());
        assertEquals(userId2, dto3.getUserId());
        assertTrue(dto3.getIsSpoiler());
        assertEquals("test search", dto3.getSearch());
        assertEquals("likeCnt", dto3.getSort());
        assertEquals("asc", dto3.getOrder());
        assertEquals(2, dto3.getPage());
        assertEquals(50, dto3.getSize());
        
        // Test all-args constructor
        CommentSearchRequestDTO dto4 = new CommentSearchRequestDTO(
            10, 300, userId2, false, "another search", "createTime", "desc", 0, 30
        );
        assertEquals(10, dto4.getChapterId());
        assertEquals(300, dto4.getNovelId());
        assertEquals(userId2, dto4.getUserId());
        assertFalse(dto4.getIsSpoiler());
        assertEquals("another search", dto4.getSearch());
        assertEquals("createTime", dto4.getSort());
        assertEquals("desc", dto4.getOrder());
        assertEquals(0, dto4.getPage());
        assertEquals(30, dto4.getSize());
    }

    @Test
    @DisplayName("Test CommentModerationStatsDTO - equals, hashCode, canEqual")
    void testCommentModerationStatsDTOEqualsHashCode() {
        CommentModerationStatsDTO dto1 = new CommentModerationStatsDTO();
        dto1.setTotalComments(1000L);
        dto1.setSpoilerComments(100L);
        dto1.setNonSpoilerComments(900L);
        dto1.setCommentsToday(50L);
        dto1.setCommentsThisWeek(300L);
        dto1.setCommentsThisMonth(1000L);
        dto1.setAvgCommentsPerChapter(10.5);
        dto1.setAvgCommentsPerUser(5.2);
        dto1.setMostActiveUserId(123);
        dto1.setMostActiveUsername("user123");
        dto1.setMostActiveUserCommentCount(500L);
        dto1.setMostCommentedChapterId(456);
        dto1.setMostCommentedChapterTitle("Chapter 1");
        dto1.setMostCommentedChapterCount(200L);
        
        CommentModerationStatsDTO dto2 = new CommentModerationStatsDTO();
        dto2.setTotalComments(1000L);
        dto2.setSpoilerComments(100L);
        dto2.setNonSpoilerComments(900L);
        dto2.setCommentsToday(50L);
        dto2.setCommentsThisWeek(300L);
        dto2.setCommentsThisMonth(1000L);
        dto2.setAvgCommentsPerChapter(10.5);
        dto2.setAvgCommentsPerUser(5.2);
        dto2.setMostActiveUserId(123);
        dto2.setMostActiveUsername("user123");
        dto2.setMostActiveUserCommentCount(500L);
        dto2.setMostCommentedChapterId(456);
        dto2.setMostCommentedChapterTitle("Chapter 1");
        dto2.setMostCommentedChapterCount(200L);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        assertTrue(dto1.canEqual(dto2));
        
        // Test with different values
        dto2.setTotalComments(2000L);
        assertNotEquals(dto1, dto2);
        
        dto2.setTotalComments(1000L);
        dto2.setMostActiveUserId(999);
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Test CommentModerationStatsDTO")
    void testCommentModerationStatsDTO() {
        CommentModerationStatsDTO dto = CommentModerationStatsDTO.builder()
            .totalComments(100L)
            .spoilerComments(10L)
            .nonSpoilerComments(90L)
            .commentsToday(5L)
            .commentsThisWeek(20L)
            .commentsThisMonth(50L)
            .avgCommentsPerChapter(5.5)
            .avgCommentsPerUser(2.3)
            .mostActiveUserId(123)
            .mostActiveUsername("active_user")
            .mostActiveUserCommentCount(30L)
            .mostCommentedChapterId(456)
            .mostCommentedChapterTitle("Chapter 10")
            .mostCommentedChapterCount(25L)
            .build();
        
        assertEquals(100L, dto.getTotalComments());
        assertEquals(10L, dto.getSpoilerComments());
        assertEquals(90L, dto.getNonSpoilerComments());
        assertEquals(5L, dto.getCommentsToday());
        assertEquals(20L, dto.getCommentsThisWeek());
        assertEquals(50L, dto.getCommentsThisMonth());
        assertEquals(5.5, dto.getAvgCommentsPerChapter());
        assertEquals(2.3, dto.getAvgCommentsPerUser());
        assertEquals(123, dto.getMostActiveUserId());
        assertEquals("active_user", dto.getMostActiveUsername());
        assertEquals(30L, dto.getMostActiveUserCommentCount());
        assertEquals(456, dto.getMostCommentedChapterId());
        assertEquals("Chapter 10", dto.getMostCommentedChapterTitle());
        assertEquals(25L, dto.getMostCommentedChapterCount());
        
        // Test no-args constructor
        CommentModerationStatsDTO dto2 = new CommentModerationStatsDTO();
        dto2.setTotalComments(200L);
        assertEquals(200L, dto2.getTotalComments());
        
        // Test all-args constructor
        CommentModerationStatsDTO dto3 = new CommentModerationStatsDTO(
            300L, 30L, 270L, 10L, 40L, 100L, 6.0, 3.0,
            789, "user789", 50L, 999, "Chapter 20", 40L
        );
        assertEquals(300L, dto3.getTotalComments());
        assertEquals(30L, dto3.getSpoilerComments());
        assertEquals(270L, dto3.getNonSpoilerComments());
        assertEquals(789, dto3.getMostActiveUserId());
        
        // Test with null values
        CommentModerationStatsDTO dto4 = new CommentModerationStatsDTO();
        dto4.setTotalComments(0L);
        dto4.setAvgCommentsPerChapter(null);
        dto4.setAvgCommentsPerUser(null);
        dto4.setMostActiveUserId(null);
        dto4.setMostActiveUsername(null);
        assertEquals(0L, dto4.getTotalComments());
        assertNull(dto4.getAvgCommentsPerChapter());
        assertNull(dto4.getAvgCommentsPerUser());
        assertNull(dto4.getMostActiveUserId());
        assertNull(dto4.getMostActiveUsername());
        
        // Test with different values to cover all branches
        CommentModerationStatsDTO dto5 = new CommentModerationStatsDTO();
        dto5.setTotalComments(500L);
        dto5.setSpoilerComments(50L);
        dto5.setNonSpoilerComments(450L);
        dto5.setCommentsToday(20L);
        dto5.setCommentsThisWeek(100L);
        dto5.setCommentsThisMonth(300L);
        dto5.setAvgCommentsPerChapter(10.0);
        dto5.setAvgCommentsPerUser(5.0);
        dto5.setMostActiveUserId(999);
        dto5.setMostActiveUsername("user999");
        dto5.setMostActiveUserCommentCount(200L);
        dto5.setMostCommentedChapterId(1111);
        dto5.setMostCommentedChapterTitle("Chapter 50");
        dto5.setMostCommentedChapterCount(150L);
        
        assertEquals(500L, dto5.getTotalComments());
        assertEquals(50L, dto5.getSpoilerComments());
        assertEquals(450L, dto5.getNonSpoilerComments());
        assertEquals(20L, dto5.getCommentsToday());
        assertEquals(100L, dto5.getCommentsThisWeek());
        assertEquals(300L, dto5.getCommentsThisMonth());
        assertEquals(10.0, dto5.getAvgCommentsPerChapter());
        assertEquals(5.0, dto5.getAvgCommentsPerUser());
        assertEquals(999, dto5.getMostActiveUserId());
        assertEquals("user999", dto5.getMostActiveUsername());
        assertEquals(200L, dto5.getMostActiveUserCommentCount());
        assertEquals(1111, dto5.getMostCommentedChapterId());
        assertEquals("Chapter 50", dto5.getMostCommentedChapterTitle());
        assertEquals(150L, dto5.getMostCommentedChapterCount());
        
        // Test equals, hashCode, and canEqual methods
        CommentModerationStatsDTO dto6 = CommentModerationStatsDTO.builder()
            .totalComments(100L)
            .spoilerComments(10L)
            .build();
        
        CommentModerationStatsDTO dto7 = CommentModerationStatsDTO.builder()
            .totalComments(100L)
            .spoilerComments(10L)
            .build();
        
        assertEquals(dto6, dto7);
        assertEquals(dto6.hashCode(), dto7.hashCode());
        assertNotEquals(dto6, null);
        assertEquals(dto6, dto6);
        assertTrue(dto6.canEqual(dto7));
    }

    @Test
    @DisplayName("Test CommentStatisticsDTO - equals, hashCode, canEqual")
    void testCommentStatisticsDTOEqualsHashCode() {
        CommentStatisticsDTO dto1 = new CommentStatisticsDTO();
        dto1.setChapterId(1);
        dto1.setChapterTitle("Chapter 1");
        dto1.setTotalComments(100L);
        dto1.setSpoilerComments(20L);
        dto1.setNonSpoilerComments(80L);
        dto1.setAvgLikesPerComment(5);
        dto1.setMostLikedCommentId(123);
        
        CommentStatisticsDTO dto2 = new CommentStatisticsDTO();
        dto2.setChapterId(1);
        dto2.setChapterTitle("Chapter 1");
        dto2.setTotalComments(100L);
        dto2.setSpoilerComments(20L);
        dto2.setNonSpoilerComments(80L);
        dto2.setAvgLikesPerComment(5);
        dto2.setMostLikedCommentId(123);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        assertTrue(dto1.canEqual(dto2));
        
        // Test with different values
        dto2.setTotalComments(200L);
        assertNotEquals(dto1, dto2);
        
        // Test toString
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentStatisticsDTO"));
        
        // Test with null values  
        CommentStatisticsDTO dto3 = new CommentStatisticsDTO();
        dto3.setChapterId(null);
        dto3.setChapterTitle(null);
        dto3.setTotalComments(50L);
        dto3.setSpoilerComments(10L);
        dto3.setNonSpoilerComments(40L);
        dto3.setAvgLikesPerComment(null);
        dto3.setMostLikedCommentId(null);
        
        CommentStatisticsDTO dto4 = new CommentStatisticsDTO();
        dto4.setChapterId(null);
        dto4.setChapterTitle(null);
        dto4.setTotalComments(50L);
        dto4.setSpoilerComments(10L);
        dto4.setNonSpoilerComments(40L);
        dto4.setAvgLikesPerComment(null);
        dto4.setMostLikedCommentId(null);
        
        assertEquals(dto3, dto4);
    }

    @Test
    @DisplayName("Test CommentStatisticsDTO")
    void testCommentStatisticsDTO() {
        CommentStatisticsDTO dto = CommentStatisticsDTO.builder()
            .chapterId(1)
            .chapterTitle("Chapter 1")
            .totalComments(500L)
            .spoilerComments(50L)
            .nonSpoilerComments(450L)
            .avgLikesPerComment(5)
            .mostLikedCommentId(123)
            .build();
        
        assertEquals(1, dto.getChapterId());
        assertEquals("Chapter 1", dto.getChapterTitle());
        assertEquals(500L, dto.getTotalComments());
        assertEquals(50L, dto.getSpoilerComments());
        assertEquals(450L, dto.getNonSpoilerComments());
        assertEquals(5, dto.getAvgLikesPerComment());
        assertEquals(123, dto.getMostLikedCommentId());
        
        // Test no-args constructor
        CommentStatisticsDTO dto2 = new CommentStatisticsDTO();
        dto2.setChapterId(2);
        dto2.setChapterTitle("Chapter 2");
        dto2.setTotalComments(600L);
        assertEquals(2, dto2.getChapterId());
        assertEquals("Chapter 2", dto2.getChapterTitle());
        assertEquals(600L, dto2.getTotalComments());
        
        // Test all-args constructor
        CommentStatisticsDTO dto3 = new CommentStatisticsDTO(
            3, "Chapter 3", 700L, 70L, 630L, 6, 456
        );
        assertEquals(3, dto3.getChapterId());
        assertEquals("Chapter 3", dto3.getChapterTitle());
        assertEquals(700L, dto3.getTotalComments());
        assertEquals(70L, dto3.getSpoilerComments());
        assertEquals(630L, dto3.getNonSpoilerComments());
        assertEquals(6, dto3.getAvgLikesPerComment());
        assertEquals(456, dto3.getMostLikedCommentId());
        
        // Test with null values
        CommentStatisticsDTO dto4 = new CommentStatisticsDTO();
        dto4.setChapterId(null);
        dto4.setChapterTitle(null);
        dto4.setTotalComments(0L);
        dto4.setAvgLikesPerComment(null);
        dto4.setMostLikedCommentId(null);
        assertNull(dto4.getChapterId());
        assertNull(dto4.getChapterTitle());
        assertEquals(0L, dto4.getTotalComments());
        assertNull(dto4.getAvgLikesPerComment());
        assertNull(dto4.getMostLikedCommentId());
        
        // Test equals, hashCode, and canEqual methods
        CommentStatisticsDTO dto5 = CommentStatisticsDTO.builder()
            .chapterId(1)
            .totalComments(100L)
            .build();
        
        CommentStatisticsDTO dto6 = CommentStatisticsDTO.builder()
            .chapterId(1)
            .totalComments(100L)
            .build();
        
        assertEquals(dto5, dto6);
        assertEquals(dto5.hashCode(), dto6.hashCode());
        assertNotEquals(dto5, null);
        assertEquals(dto5, dto5);
        assertTrue(dto5.canEqual(dto6));
    }

    @Test
    @DisplayName("Test CommentListResponseDTO - equals, hashCode, canEqual")
    void testCommentListResponseDTOEqualsHashCode() {
        CommentListResponseDTO dto1 = new CommentListResponseDTO();
        dto1.setComments(Arrays.asList());
        dto1.setTotalCount(100L);
        dto1.setTotalPages(10);
        dto1.setCurrentPage(1);
        dto1.setPageSize(10);
        
        CommentListResponseDTO dto2 = new CommentListResponseDTO();
        dto2.setComments(Arrays.asList());
        dto2.setTotalCount(100L);
        dto2.setTotalPages(10);
        dto2.setCurrentPage(1);
        dto2.setPageSize(10);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        assertTrue(dto1.canEqual(dto2));
        
        // Test with different values
        CommentListResponseDTO dto3 = new CommentListResponseDTO();
        dto3.setTotalCount(200L);
        dto3.setTotalPages(20);
        assertNotEquals(dto1, dto3);
        
        // Test with null comments
        dto1.setComments(null);
        dto2.setComments(null);
        assertEquals(dto1, dto2);
        
        dto1.setComments(Arrays.asList());
        assertNotEquals(dto1, dto2);
        
        // Test toString
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentListResponseDTO"));
    }

    @Test
    @DisplayName("Test CommentListResponseDTO")
    void testCommentListResponseDTO() {
        CommentResponseDTO comment1 = new CommentResponseDTO();
        comment1.setId(1);
        comment1.setContent("Comment 1");
        
        CommentResponseDTO comment2 = new CommentResponseDTO();
        comment2.setId(2);
        comment2.setContent("Comment 2");
        
        List<CommentResponseDTO> comments = new java.util.ArrayList<>(Arrays.asList(comment1, comment2));
        CommentListResponseDTO dto = new CommentListResponseDTO(comments, 2L, 1, 0, 20);
        
        assertEquals(2, dto.getComments().size());
        assertEquals(2L, dto.getTotalCount());
        assertEquals(1, dto.getTotalPages());
        assertEquals(0, dto.getCurrentPage());
        assertEquals(20, dto.getPageSize());
        
        // Test defensive copying
        comments.add(new CommentResponseDTO());
        assertEquals(2, dto.getComments().size());
        
        List<CommentResponseDTO> retrieved = dto.getComments();
        assertNotSame(comments, retrieved);
    }

    @Test
    @DisplayName("Test CommentListResponseDTO builder")
    void testCommentListResponseDTOBuilder() {
        CommentResponseDTO comment = new CommentResponseDTO();
        comment.setId(1);
        comment.setContent("Test");
        
        List<CommentResponseDTO> comments = new java.util.ArrayList<>(Arrays.asList(comment));
        CommentListResponseDTO dto = CommentListResponseDTO.builder()
            .comments(comments)
            .totalCount(1L)
            .totalPages(1)
            .currentPage(0)
            .pageSize(20)
            .build();
        
        assertEquals(1, dto.getComments().size());
        assertEquals(1L, dto.getTotalCount());
        assertEquals(1, dto.getTotalPages());
        assertEquals(0, dto.getCurrentPage());
        assertEquals(20, dto.getPageSize());
        
        // Test defensive copying in builder
        comments.add(new CommentResponseDTO());
        assertEquals(1, dto.getComments().size());
        
        // Test default constructor
        CommentListResponseDTO dto2 = new CommentListResponseDTO();
        dto2.setComments(comments);
        dto2.setTotalCount(2L);
        dto2.setTotalPages(1);
        dto2.setCurrentPage(0);
        dto2.setPageSize(20);
        assertEquals(2, dto2.getComments().size());
        assertEquals(2L, dto2.getTotalCount());
        assertEquals(1, dto2.getTotalPages());
        assertEquals(0, dto2.getCurrentPage());
        assertEquals(20, dto2.getPageSize());
        
        // Test getComments with null check branch
        CommentListResponseDTO dto3 = new CommentListResponseDTO();
        assertNull(dto3.getComments());
        
        CommentResponseDTO comment3 = new CommentResponseDTO();
        comment3.setId(3);
        comment3.setContent("Comment 3");
        CommentResponseDTO comment4 = new CommentResponseDTO();
        comment4.setId(4);
        comment4.setContent("Comment 4");
        
        List<CommentResponseDTO> comments3 = new java.util.ArrayList<>(Arrays.asList(comment3, comment4));
        dto3.setComments(comments3);
        List<CommentResponseDTO> retrieved = dto3.getComments();
        assertNotNull(retrieved);
        assertNotSame(comments3, retrieved);
        
        // Test setComments with null parameter branch
        dto3.setComments(null);
        assertNull(dto3.getComments());
        
        // Test setComments with non-null parameter branch
        List<CommentResponseDTO> comments4 = new java.util.ArrayList<>(Arrays.asList(comment3));
        dto3.setComments(comments4);
        List<CommentResponseDTO> retrieved2 = dto3.getComments();
        assertNotNull(retrieved2);
        assertNotSame(comments4, retrieved2);
        
        // Test builder with null check branch
        CommentListResponseDTO dto4 = CommentListResponseDTO.builder()
            .comments(null)
            .totalCount(0L)
            .totalPages(0)
            .currentPage(0)
            .pageSize(20)
            .build();
        assertNull(dto4.getComments());
        
        // Test builder with non-null comments
        List<CommentResponseDTO> comments5 = new java.util.ArrayList<>(Arrays.asList(comment3, comment4));
        CommentListResponseDTO dto5 = CommentListResponseDTO.builder()
            .comments(comments5)
            .totalCount(2L)
            .totalPages(1)
            .currentPage(0)
            .pageSize(20)
            .build();
        List<CommentResponseDTO> retrieved3 = dto5.getComments();
        assertNotNull(retrieved3);
        assertNotSame(comments5, retrieved3);
        
        // Test null comments
        dto2.setComments(null);
        assertNull(dto2.getComments());
    }

    @Test
    @DisplayName("Test CommentResponseDTO")
    void testCommentResponseDTO() {
        Date now = new Date();
        UUID userId = UUID.randomUUID();
        
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(1);
        dto.setChapterId(10);
        dto.setChapterTitle("Chapter 10");
        dto.setContent("Test comment");
        dto.setLikeCnt(5);
        dto.setIsSpoiler(false);
        dto.setUserId(userId);
        dto.setUsername("testuser");
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        dto.setIsOwnComment(true);
        
        assertEquals(1, dto.getId());
        assertEquals(10, dto.getChapterId());
        assertEquals("Chapter 10", dto.getChapterTitle());
        assertEquals("Test comment", dto.getContent());
        assertEquals(5, dto.getLikeCnt());
        assertFalse(dto.getIsSpoiler());
        assertEquals(userId, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertTrue(dto.getIsOwnComment());
        
        // Test defensive copying for Date fields
        Date testDate = new Date();
        dto.setCreateTime(testDate);
        Date retrieved = dto.getCreateTime();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        dto.setUpdateTime(testDate);
        retrieved = dto.getUpdateTime();
        assertNotSame(testDate, retrieved);
        
        // Test constructor with all args
        Date now2 = new Date();
        UUID userId2 = UUID.randomUUID();
        CommentResponseDTO dto2 = new CommentResponseDTO(
            2, userId2, "user2", 20, "Chapter 20", "Content 2", 
            10, true, now2, now2, false
        );
        assertEquals(2, dto2.getId());
        assertEquals(userId2, dto2.getUserId());
        assertEquals("user2", dto2.getUsername());
        assertEquals(20, dto2.getChapterId());
        assertEquals("Chapter 20", dto2.getChapterTitle());
        assertEquals("Content 2", dto2.getContent());
        assertEquals(10, dto2.getLikeCnt());
        assertTrue(dto2.getIsSpoiler());
        assertFalse(dto2.getIsOwnComment());
        assertNotNull(dto2.getCreateTime());
        assertNotNull(dto2.getUpdateTime());
        assertNotSame(now2, dto2.getCreateTime());
        assertNotSame(now2, dto2.getUpdateTime());
        
        // Test builder
        Date now3 = new Date();
        UUID userId3 = UUID.randomUUID();
        CommentResponseDTO dto3 = CommentResponseDTO.builder()
            .id(3)
            .userId(userId3)
            .username("user3")
            .chapterId(30)
            .chapterTitle("Chapter 30")
            .content("Content 3")
            .likeCnt(15)
            .isSpoiler(false)
            .createTime(now3)
            .updateTime(now3)
            .isOwnComment(true)
            .build();
        assertEquals(3, dto3.getId());
        assertEquals(userId3, dto3.getUserId());
        assertEquals("user3", dto3.getUsername());
        assertEquals(30, dto3.getChapterId());
        assertEquals("Chapter 30", dto3.getChapterTitle());
        assertEquals("Content 3", dto3.getContent());
        assertEquals(15, dto3.getLikeCnt());
        assertFalse(dto3.getIsSpoiler());
        assertTrue(dto3.getIsOwnComment());
        assertNotNull(dto3.getCreateTime());
        assertNotNull(dto3.getUpdateTime());
        assertNotSame(now3, dto3.getCreateTime());
        assertNotSame(now3, dto3.getUpdateTime());
        
        // Test null Date values
        dto.setCreateTime(null);
        dto.setUpdateTime(null);
        assertNull(dto.getCreateTime());
        assertNull(dto.getUpdateTime());
    }

    @Test
    @DisplayName("Test CommentResponseDTO constructor")
    void testCommentResponseDTOConstructor() {
        Date now = new Date();
        UUID userId = UUID.randomUUID();
        
        CommentResponseDTO dto = new CommentResponseDTO(
            1, userId, "user", 10, "Chapter 10", "Content", 
            5, false, now, now, true
        );
        
        assertEquals(1, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals("user", dto.getUsername());
        assertEquals(10, dto.getChapterId());
        assertEquals("Chapter 10", dto.getChapterTitle());
        assertEquals("Content", dto.getContent());
        assertEquals(5, dto.getLikeCnt());
        assertFalse(dto.getIsSpoiler());
        assertTrue(dto.getIsOwnComment());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
    }

    @Test
    @DisplayName("Test CommentResponseDTO builder")
    void testCommentResponseDTOBuilder() {
        Date now = new Date();
        UUID userId = UUID.randomUUID();
        
        CommentResponseDTO dto = CommentResponseDTO.builder()
            .id(1)
            .userId(userId)
            .username("builder_user")
            .chapterId(5)
            .chapterTitle("Chapter 5")
            .content("Builder test")
            .likeCnt(10)
            .isSpoiler(true)
            .createTime(now)
            .updateTime(now)
            .isOwnComment(false)
            .build();
        
        assertEquals(1, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals("builder_user", dto.getUsername());
        assertEquals(5, dto.getChapterId());
        assertEquals("Chapter 5", dto.getChapterTitle());
        assertEquals("Builder test", dto.getContent());
        assertEquals(10, dto.getLikeCnt());
        assertTrue(dto.getIsSpoiler());
        assertFalse(dto.getIsOwnComment());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        
        // Test defensive copying in builder
        Date testDate = new Date();
        CommentResponseDTO dto2 = CommentResponseDTO.builder()
            .createTime(testDate)
            .updateTime(testDate)
            .build();
        
        Date retrieved = dto2.getCreateTime();
        assertNotSame(testDate, retrieved);
        
        // Test equals, hashCode, and canEqual methods for CommentResponseDTO
        UUID userId3 = UUID.randomUUID();
        Date now3 = new Date();
        CommentResponseDTO dto3 = CommentResponseDTO.builder()
            .id(1)
            .userId(userId3)
            .username("test")
            .chapterId(1)
            .chapterTitle("Chapter 1")
            .content("Content")
            .likeCnt(0)
            .isSpoiler(false)
            .createTime(now3)
            .updateTime(now3)
            .isOwnComment(false)
            .build();
        
        CommentResponseDTO dto4 = CommentResponseDTO.builder()
            .id(1)
            .userId(userId3)
            .username("test")
            .chapterId(1)
            .chapterTitle("Chapter 1")
            .content("Content")
            .likeCnt(0)
            .isSpoiler(false)
            .createTime(now3)
            .updateTime(now3)
            .isOwnComment(false)
            .build();
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
        
        // Test with different values
        dto4.setId(2);
        assertNotEquals(dto3, dto4);
    }

    @Test
    @DisplayName("Test CommentBulkSpoilerUpdateRequestDTO")
    void testCommentBulkSpoilerUpdateRequestDTO() {
        CommentBulkSpoilerUpdateRequestDTO dto = new CommentBulkSpoilerUpdateRequestDTO();
        java.util.List<Integer> ids = new java.util.ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        dto.setCommentIds(ids);
        dto.setIsSpoiler(true);
        
        assertEquals(5, dto.getCommentIds().size());
        assertTrue(dto.getIsSpoiler());
        
        // Test defensive copying
        java.util.List<Integer> original = new java.util.ArrayList<>(Arrays.asList(10, 20));
        dto.setCommentIds(original);
        java.util.List<Integer> retrieved = dto.getCommentIds();
        assertNotSame(original, retrieved);
        original.add(30);
        assertEquals(2, retrieved.size());
        
        // Test with false spoiler
        dto.setIsSpoiler(false);
        assertFalse(dto.getIsSpoiler());
        
        // Test with null ids (should handle gracefully)
        dto.setCommentIds(null);
        assertNull(dto.getCommentIds());
        
        // Test getCommentIds with null check branch
        CommentBulkSpoilerUpdateRequestDTO dto2 = new CommentBulkSpoilerUpdateRequestDTO();
        assertNull(dto2.getCommentIds());
        
        java.util.List<Integer> ids2 = new java.util.ArrayList<>(Arrays.asList(6, 7, 8));
        dto2.setCommentIds(ids2);
        java.util.List<Integer> retrieved2 = dto2.getCommentIds();
        assertNotNull(retrieved2);
        assertNotSame(ids2, retrieved2);
        
        // Test setCommentIds with null parameter branch
        dto2.setCommentIds(null);
        assertNull(dto2.getCommentIds());
        
        // Test setCommentIds with non-null parameter branch
        java.util.List<Integer> ids3 = new java.util.ArrayList<>(Arrays.asList(9, 10));
        dto2.setCommentIds(ids3);
        java.util.List<Integer> retrieved3 = dto2.getCommentIds();
        assertNotNull(retrieved3);
        assertNotSame(ids3, retrieved3);
        
        // Test equals, hashCode, and canEqual methods for CommentListResponseDTO
        CommentListResponseDTO listDto1 = new CommentListResponseDTO();
        List<CommentResponseDTO> comments1 = new java.util.ArrayList<>();
        listDto1.setComments(comments1);
        listDto1.setTotalCount(0);
        
        CommentListResponseDTO listDto2 = new CommentListResponseDTO();
        List<CommentResponseDTO> comments2 = new java.util.ArrayList<>();
        listDto2.setComments(comments2);
        listDto2.setTotalCount(0);
        
        assertEquals(listDto1, listDto2);
        assertEquals(listDto1.hashCode(), listDto2.hashCode());
        assertNotEquals(listDto1, null);
        assertEquals(listDto1, listDto1);
        assertTrue(listDto1.canEqual(listDto2));
        
        // Test equals, hashCode, and canEqual methods for CommentSearchRequestDTO
        CommentSearchRequestDTO searchDto1 = new CommentSearchRequestDTO();
        searchDto1.setChapterId(1);
        searchDto1.setPage(0);
        searchDto1.setSize(20);
        
        CommentSearchRequestDTO searchDto2 = new CommentSearchRequestDTO();
        searchDto2.setChapterId(1);
        searchDto2.setPage(0);
        searchDto2.setSize(20);
        
        assertEquals(searchDto1, searchDto2);
        assertEquals(searchDto1.hashCode(), searchDto2.hashCode());
        assertNotEquals(searchDto1, null);
        assertEquals(searchDto1, searchDto1);
        assertTrue(searchDto1.canEqual(searchDto2));
        
        // Test equals, hashCode, and canEqual methods
        CommentBulkSpoilerUpdateRequestDTO dto6 = new CommentBulkSpoilerUpdateRequestDTO();
        dto6.setIsSpoiler(true);
        java.util.List<Integer> ids6 = new java.util.ArrayList<>(Arrays.asList(1, 2));
        dto6.setCommentIds(ids6);
        
        CommentBulkSpoilerUpdateRequestDTO dto7 = new CommentBulkSpoilerUpdateRequestDTO();
        dto7.setIsSpoiler(true);
        java.util.List<Integer> ids7 = new java.util.ArrayList<>(Arrays.asList(1, 2));
        dto7.setCommentIds(ids7);
        
        // Test equals - same values
        assertEquals(dto6, dto7);
        assertEquals(dto6.hashCode(), dto7.hashCode());
        
        // Test equals - different values
        dto7.setIsSpoiler(false);
        assertNotEquals(dto6, dto7);
        
        // Test equals - null
        assertNotEquals(dto6, null);
        
        // Test equals - same object
        assertEquals(dto6, dto6);
        
        // Test canEqual
        assertTrue(dto6.canEqual(dto7));
    }
}

