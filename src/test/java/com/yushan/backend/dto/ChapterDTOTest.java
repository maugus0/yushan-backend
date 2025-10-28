package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Chapter DTO Tests")
class ChapterDTOTest {

    @Test
    @DisplayName("Test ChapterDetailResponseDTO")
    void testChapterDetailResponseDTO() {
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        
        ChapterDetailResponseDTO dto = new ChapterDetailResponseDTO();
        dto.setId(1);
        dto.setUuid(uuid);
        dto.setNovelId(100);
        dto.setChapterNumber(5);
        dto.setTitle("Chapter 5");
        dto.setContent("Chapter content");
        dto.setWordCnt(1000);
        dto.setIsPremium(false);
        dto.setYuanCost(0.0f);
        dto.setViewCnt(50L);
        dto.setIsValid(true);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        dto.setPublishTime(now);
        
        assertEquals(1, dto.getId());
        assertEquals(uuid, dto.getUuid());
        assertEquals(100, dto.getNovelId());
        assertEquals(5, dto.getChapterNumber());
        assertEquals("Chapter 5", dto.getTitle());
        assertEquals("Chapter content", dto.getContent());
        assertEquals(1000, dto.getWordCnt());
        assertFalse(dto.getIsPremium());
        assertEquals(0.0f, dto.getYuanCost());
        assertEquals(50L, dto.getViewCnt());
        assertTrue(dto.getIsValid());
        assertNotNull(dto.getCreateTime());
    }

    @Test
    @DisplayName("Test ChapterListResponseDTO")
    void testChapterListResponseDTO() {
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        
        ChapterListResponseDTO.ChapterSummary summary = new ChapterListResponseDTO.ChapterSummary();
        summary.setChapterId(1);
        summary.setUuid(uuid);
        summary.setChapterNumber(1);
        summary.setTitle("Chapter 1");
        summary.setWordCnt(500);
        summary.setIsPremium(false);
        summary.setViewCnt(100L);
        summary.setPublishTime(now);
        
        List<ChapterListResponseDTO.ChapterSummary> chapters = Arrays.asList(summary);
        ChapterListResponseDTO dto = new ChapterListResponseDTO(chapters, 1L, 0, 20, 1);
        
        assertEquals(1, dto.getChapters().size());
        assertEquals(1L, dto.getTotalCount());
        assertEquals(0, dto.getCurrentPage());
        assertEquals(20, dto.getPageSize());
        assertEquals(1, dto.getTotalPages());
        
        // Test defensive copying for List
        java.util.List<ChapterListResponseDTO.ChapterSummary> original = 
            new java.util.ArrayList<>(dto.getChapters());
        original.add(new ChapterListResponseDTO.ChapterSummary());
        assertEquals(1, dto.getChapters().size());
        
        // Test defensive copying for Date in ChapterSummary
        Date testDate = new Date();
        summary.setPublishTime(testDate);
        Date retrieved = summary.getPublishTime();
        assertNotSame(testDate, retrieved);
    }

    @Test
    @DisplayName("Test ChapterListResponseDTO.ChapterSummary")
    void testChapterSummary() {
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        
        ChapterListResponseDTO.ChapterSummary summary = new ChapterListResponseDTO.ChapterSummary(
            1, uuid, 5, "Chapter 5", "Preview...", 1000, false, 0.0f, 100L, now
        );
        
        assertEquals(1, summary.getChapterId());
        assertEquals(uuid, summary.getUuid());
        assertEquals(5, summary.getChapterNumber());
        assertEquals("Chapter 5", summary.getTitle());
        assertEquals("Preview...", summary.getContentPreview());
        assertEquals(1000, summary.getWordCnt());
        assertFalse(summary.getIsPremium());
        assertNotNull(summary.getPublishTime());
    }

    @Test
    @DisplayName("Test ChapterStatisticsResponseDTO")
    void testChapterStatisticsResponseDTO() {
        ChapterStatisticsResponseDTO dto = new ChapterStatisticsResponseDTO(
            1, 10L, 8L, 1L, 1L, 5L, 5L, 50000L, 10000L, 500.0f, 10
        );
        
        assertEquals(1, dto.getNovelId());
        assertEquals(10L, dto.getTotalChapters());
        assertEquals(8L, dto.getPublishedChapters());
        assertEquals(1L, dto.getDraftChapters());
        assertEquals(1L, dto.getScheduledChapters());
        assertEquals(5L, dto.getPremiumChapters());
        assertEquals(5L, dto.getFreeChapters());
        assertEquals(50000L, dto.getTotalWordCount());
        assertEquals(10000L, dto.getTotalViewCount());
        assertEquals(500.0f, dto.getTotalRevenue());
        assertEquals(10, dto.getMaxChapterNumber());
        
        // Test defensive copying for nested ChapterSummary
        ChapterStatisticsResponseDTO.ChapterSummary latest = 
            new ChapterStatisticsResponseDTO.ChapterSummary(10, "Latest Chapter", 5000L);
        dto.setLatestChapter(latest);
        ChapterStatisticsResponseDTO.ChapterSummary retrieved = dto.getLatestChapter();
        assertNotSame(latest, retrieved);
        assertEquals(10, retrieved.getChapterNumber());
        assertEquals("Latest Chapter", retrieved.getTitle());
        assertEquals(5000L, retrieved.getViewCnt());
        
        ChapterStatisticsResponseDTO.ChapterSummary mostViewed = 
            new ChapterStatisticsResponseDTO.ChapterSummary(5, "Most Viewed", 10000L);
        dto.setMostViewedChapter(mostViewed);
        retrieved = dto.getMostViewedChapter();
        assertNotSame(mostViewed, retrieved);
        assertEquals(5, retrieved.getChapterNumber());
        assertEquals("Most Viewed", retrieved.getTitle());
        assertEquals(10000L, retrieved.getViewCnt());
    }

    @Test
    @DisplayName("Test ChapterStatisticsResponseDTO.ChapterSummary")
    void testChapterStatisticsSummary() {
        ChapterStatisticsResponseDTO.ChapterSummary summary = 
            new ChapterStatisticsResponseDTO.ChapterSummary(5, "Chapter 5", 1000L);
        
        assertEquals(5, summary.getChapterNumber());
        assertEquals("Chapter 5", summary.getTitle());
        assertEquals(1000L, summary.getViewCnt());
        
        // Test copy constructor
        ChapterStatisticsResponseDTO.ChapterSummary copy = 
            new ChapterStatisticsResponseDTO.ChapterSummary(summary);
        
        assertEquals(5, copy.getChapterNumber());
        assertEquals("Chapter 5", copy.getTitle());
        assertEquals(1000L, copy.getViewCnt());
    }

    @Test
    @DisplayName("Test ChapterDetailResponseDTO with navigation")
    void testChapterDetailResponseDTOWithNavigation() {
        Date now = new Date();
        UUID chapterUuid = UUID.randomUUID();
        UUID prevUuid = UUID.randomUUID();
        UUID nextUuid = UUID.randomUUID();
        
        ChapterDetailResponseDTO dto = new ChapterDetailResponseDTO();
        dto.setId(1);
        dto.setUuid(chapterUuid);
        dto.setNovelId(100);
        dto.setChapterNumber(10);
        dto.setTitle("Chapter 10");
        dto.setContent("Content");
        dto.setWordCnt(1000);
        dto.setIsPremium(false);
        dto.setYuanCost(0.0f);
        dto.setViewCnt(100L);
        dto.setIsValid(true);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        dto.setPublishTime(now);
        dto.setPreviousChapterUuid(prevUuid);
        dto.setNextChapterUuid(nextUuid);
        
        assertEquals(prevUuid, dto.getPreviousChapterUuid());
        assertEquals(nextUuid, dto.getNextChapterUuid());
        assertEquals(1, dto.getId());
        assertEquals(chapterUuid, dto.getUuid());
        assertEquals(100, dto.getNovelId());
        assertEquals(10, dto.getChapterNumber());
        assertEquals("Chapter 10", dto.getTitle());
        assertEquals("Content", dto.getContent());
        assertEquals(1000, dto.getWordCnt());
        assertFalse(dto.getIsPremium());
        assertEquals(0.0f, dto.getYuanCost());
        assertEquals(100L, dto.getViewCnt());
        assertTrue(dto.getIsValid());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertNotNull(dto.getPublishTime());
        
        // Test defensive copying
        Date testDate = new Date();
        dto.setCreateTime(testDate);
        Date retrieved = dto.getCreateTime();
        assertNotSame(testDate, retrieved);
    }

    @Test
    @DisplayName("Test ChapterDetailResponseDTO constructor")
    void testChapterDetailResponseDTOConstructor() {
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        
        ChapterDetailResponseDTO dto = new ChapterDetailResponseDTO(
            1, uuid, 100, 10, "Chapter 10", "Content", 
            1000, false, 0.0f, 100L, true, now, now, now
        );
        
        assertEquals(1, dto.getId());
        assertEquals(uuid, dto.getUuid());
        assertEquals(100, dto.getNovelId());
        assertEquals(10, dto.getChapterNumber());
        assertEquals("Chapter 10", dto.getTitle());
        assertEquals("Content", dto.getContent());
        assertEquals(1000, dto.getWordCnt());
        assertFalse(dto.getIsPremium());
        assertEquals(0.0f, dto.getYuanCost());
        assertEquals(100L, dto.getViewCnt());
        assertTrue(dto.getIsValid());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertNotNull(dto.getPublishTime());
        assertNotSame(now, dto.getCreateTime());
        assertNotSame(now, dto.getUpdateTime());
        assertNotSame(now, dto.getPublishTime());
    }

    @Test
    @DisplayName("Test ChapterCreateRequestDTO")
    void testChapterCreateRequestDTO() {
        Date now = new Date();
        
        ChapterCreateRequestDTO dto = new ChapterCreateRequestDTO(
            1, 5, "Chapter 5", "Content here", 1000, false, 0.0f, true, now
        );
        
        assertEquals(1, dto.getNovelId());
        assertEquals(5, dto.getChapterNumber());
        assertEquals("Chapter 5", dto.getTitle());
        assertEquals("Content here", dto.getContent());
        assertEquals(1000, dto.getWordCnt());
        assertFalse(dto.getIsPremium());
        assertEquals(0.0f, dto.getYuanCost());
        assertTrue(dto.getIsValid());
        assertNotNull(dto.getPublishTime());
        
        // Test defensive copying for publishTime
        Date testDate = new Date();
        dto.setPublishTime(testDate);
        Date retrieved = dto.getPublishTime();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        // Test title trimming
        dto.setTitle("  Test Title  ");
        assertEquals("Test Title", dto.getTitle());
        
        dto.setTitle(null);
        assertNull(dto.getTitle());
        
        // Test getPublishTime with null check branch
        dto.setPublishTime(null);
        assertNull(dto.getPublishTime());
        
        // Test setPublishTime with null parameter branch
        dto.setPublishTime(null);
        assertNull(dto.getPublishTime());
        
        // Test setPublishTime with non-null parameter branch
        Date newDate = new Date();
        dto.setPublishTime(newDate);
        Date retrieved2 = dto.getPublishTime();
        assertNotNull(retrieved2);
        assertNotSame(newDate, retrieved2);
        
        // Test default constructor
        ChapterCreateRequestDTO dto2 = new ChapterCreateRequestDTO();
        dto2.setNovelId(2);
        dto2.setChapterNumber(10);
        dto2.setTitle("Title");
        dto2.setContent("Content");
        dto2.setWordCnt(500);
        dto2.setIsPremium(true);
        dto2.setYuanCost(5.5f);
        dto2.setIsValid(false);
        dto2.setPublishTime(now);
        
        assertEquals(2, dto2.getNovelId());
        assertEquals(10, dto2.getChapterNumber());
        assertEquals("Title", dto2.getTitle());
        assertEquals(500, dto2.getWordCnt());
        assertTrue(dto2.getIsPremium());
        assertEquals(5.5f, dto2.getYuanCost());
        assertFalse(dto2.getIsValid());
    }

    @Test
    @DisplayName("Test ChapterPublishRequestDTO")
    void testChapterPublishRequestDTO() {
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        
        ChapterPublishRequestDTO dto = new ChapterPublishRequestDTO(uuid, true, now);
        
        assertEquals(uuid, dto.getUuid());
        assertTrue(dto.getIsValid());
        assertNotNull(dto.getPublishTime());
        
        // Test defensive copying
        Date testDate = new Date();
        dto.setPublishTime(testDate);
        Date retrieved = dto.getPublishTime();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        // Test default constructor
        ChapterPublishRequestDTO dto2 = new ChapterPublishRequestDTO();
        UUID uuid2 = UUID.randomUUID();
        dto2.setUuid(uuid2);
        dto2.setIsValid(false);
        dto2.setPublishTime(now);
        assertEquals(uuid2, dto2.getUuid());
        assertFalse(dto2.getIsValid());
        assertNotNull(dto2.getPublishTime());
        
        // Test null Date branches
        dto2.setPublishTime(null);
        assertNull(dto2.getPublishTime());
        
        Date newDate = new Date();
        dto2.setPublishTime(newDate);
        Date retrieved2 = dto2.getPublishTime();
        assertNotNull(retrieved2);
        assertNotSame(newDate, retrieved2);
        
        // Test constructor with null Date
        ChapterPublishRequestDTO dto3 = new ChapterPublishRequestDTO(uuid2, true, null);
        assertEquals(uuid2, dto3.getUuid());
        assertTrue(dto3.getIsValid());
        assertNull(dto3.getPublishTime());
    }

    @Test
    @DisplayName("Test ChapterUpdateRequestDTO")
    void testChapterUpdateRequestDTO() {
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        
        ChapterUpdateRequestDTO dto = new ChapterUpdateRequestDTO(
            uuid, "Updated Title", "Updated Content", 2000, 
            true, 10.0f, true, now
        );
        
        assertEquals(uuid, dto.getUuid());
        assertEquals("Updated Title", dto.getTitle());
        assertEquals("Updated Content", dto.getContent());
        assertEquals(2000, dto.getWordCnt());
        assertTrue(dto.getIsPremium());
        assertEquals(10.0f, dto.getYuanCost());
        assertTrue(dto.getIsValid());
        assertNotNull(dto.getPublishTime());
        
        // Test defensive copying
        Date date = new Date();
        dto.setPublishTime(date);
        Date retrieved = dto.getPublishTime();
        assertNotSame(date, retrieved);
        
        // Test title trimming
        dto.setTitle("  Trimmed Title  ");
        assertEquals("Trimmed Title", dto.getTitle());
        
        dto.setTitle(null);
        assertNull(dto.getTitle());
        
        // Test default constructor
        ChapterUpdateRequestDTO dto2 = new ChapterUpdateRequestDTO();
        UUID uuid2 = UUID.randomUUID();
        dto2.setUuid(uuid2);
        dto2.setTitle("Title");
        dto2.setContent("Content");
        dto2.setWordCnt(1000);
        dto2.setIsPremium(false);
        dto2.setYuanCost(0.0f);
        dto2.setIsValid(true);
        dto2.setPublishTime(now);
        
        assertEquals(uuid2, dto2.getUuid());
        assertEquals("Title", dto2.getTitle());
        assertEquals("Content", dto2.getContent());
        assertEquals(1000, dto2.getWordCnt());
        assertFalse(dto2.getIsPremium());
        assertEquals(0.0f, dto2.getYuanCost());
        assertTrue(dto2.getIsValid());
        assertNotNull(dto2.getPublishTime());
        
        // Test null Date branches
        dto2.setPublishTime(null);
        assertNull(dto2.getPublishTime());
        
        Date newDate = new Date();
        dto2.setPublishTime(newDate);
        Date retrieved2 = dto2.getPublishTime();
        assertNotNull(retrieved2);
        assertNotSame(newDate, retrieved2);
    }

    @Test
    @DisplayName("Test ChapterBatchCreateRequestDTO")
    void testChapterBatchCreateRequestDTO() {
        Date now = new Date();
        
        ChapterBatchCreateRequestDTO.ChapterData chapter1 = 
            new ChapterBatchCreateRequestDTO.ChapterData(1, "Chapter 1", "Content", 
                500, false, 0.0f, true, now);
        ChapterBatchCreateRequestDTO.ChapterData chapter2 = 
            new ChapterBatchCreateRequestDTO.ChapterData(2, "Chapter 2", "Content", 
                600, false, 0.0f, true, now);
        
        java.util.List<ChapterBatchCreateRequestDTO.ChapterData> chapters = 
            new java.util.ArrayList<>(Arrays.asList(chapter1, chapter2));
        
        ChapterBatchCreateRequestDTO dto = new ChapterBatchCreateRequestDTO(1, chapters);
        
        assertEquals(1, dto.getNovelId());
        assertEquals(2, dto.getChapters().size());
        assertEquals("Chapter 1", dto.getChapters().get(0).getTitle());
        assertEquals("Chapter 2", dto.getChapters().get(1).getTitle());
        
        // Test defensive copying
        chapters.add(new ChapterBatchCreateRequestDTO.ChapterData());
        assertEquals(2, dto.getChapters().size());
        
        // Test ChapterData defensive copying for Date
        Date testDate = new Date();
        chapter1.setPublishTime(testDate);
        Date retrieved = chapter1.getPublishTime();
        assertNotSame(testDate, retrieved);
        
        // Test ChapterData title trimming
        chapter1.setTitle("  Trimmed Title  ");
        assertEquals("Trimmed Title", chapter1.getTitle());
        
        // Test ChapterData default values
        ChapterBatchCreateRequestDTO.ChapterData chapter3 = new ChapterBatchCreateRequestDTO.ChapterData();
        chapter3.setChapterNumber(3);
        chapter3.setTitle("Chapter 3");
        chapter3.setContent("Content 3");
        assertEquals(3, chapter3.getChapterNumber());
        assertEquals("Chapter 3", chapter3.getTitle());
        assertEquals("Content 3", chapter3.getContent());
        assertFalse(chapter3.getIsPremium()); // Default false
        assertEquals(0.0f, chapter3.getYuanCost()); // Default 0.0f
        assertTrue(chapter3.getIsValid()); // Default true
        
        // Test default constructor
        ChapterBatchCreateRequestDTO dto2 = new ChapterBatchCreateRequestDTO();
        dto2.setNovelId(2);
        dto2.setChapters(chapters);
        assertEquals(2, dto2.getNovelId());
        assertEquals(3, dto2.getChapters().size());
    }

    @Test
    @DisplayName("Test ChapterSearchRequestDTO")
    void testChapterSearchRequestDTO() {
        // Test constructor with all fields
        ChapterSearchRequestDTO dto = new ChapterSearchRequestDTO(
            1, 5, "test", false, true, true, 1, 20, "chapterNumber", "asc"
        );
        
        assertEquals(1, dto.getNovelId());
        assertEquals(5, dto.getChapterNumber());
        assertEquals("test", dto.getTitleKeyword());
        assertFalse(dto.getIsPremium());
        assertTrue(dto.getIsValid());
        assertTrue(dto.getPublishedOnly());
        assertEquals(1, dto.getPage());
        assertEquals(20, dto.getPageSize());
        assertEquals("chapterNumber", dto.getSortBy());
        assertEquals("asc", dto.getSortOrder());
        assertEquals(0, dto.getOffset()); // (1-1)*20 = 0
        
        // Test default constructor
        ChapterSearchRequestDTO dto2 = new ChapterSearchRequestDTO();
        assertEquals(1, dto2.getPage());
        assertEquals(20, dto2.getPageSize());
        assertEquals("chapterNumber", dto2.getSortBy());
        assertEquals("asc", dto2.getSortOrder());
        
        // Test getOffset helper method
        assertEquals(0, dto2.getOffset()); // (1-1) * 20 = 0
        
        dto2.setPage(2);
        dto2.setPageSize(10);
        assertEquals(10, dto2.getOffset()); // (2-1) * 10 = 10
        
        dto2.setPage(5);
        dto2.setPageSize(25);
        assertEquals(100, dto2.getOffset()); // (5-1) * 25 = 100
        
        // Test title trimming
        dto2.setTitleKeyword("  Test Title  ");
        assertEquals("Test Title", dto2.getTitleKeyword());
        
        dto2.setTitleKeyword(null);
        assertNull(dto2.getTitleKeyword());
        
        // Test all-args constructor with different values
        ChapterSearchRequestDTO dto3 = new ChapterSearchRequestDTO(
            999, 50, "Different Title", true, false, false, 10, 50, "titleKeyword", "desc"
        );
        assertEquals(999, dto3.getNovelId());
        assertEquals(50, dto3.getChapterNumber());
        assertEquals("Different Title", dto3.getTitleKeyword());
        assertTrue(dto3.getIsPremium());
        assertFalse(dto3.getIsValid());
        assertFalse(dto3.getPublishedOnly());
        assertEquals(10, dto3.getPage());
        assertEquals(50, dto3.getPageSize());
        assertEquals("titleKeyword", dto3.getSortBy());
        assertEquals("desc", dto3.getSortOrder());
        assertEquals(450, dto3.getOffset()); // (10-1) * 50 = 450
        
        // Test setters
        dto2.setNovelId(100);
        dto2.setChapterNumber(10);
        dto2.setTitleKeyword("  title  ");
        dto2.setIsPremium(true);
        dto2.setIsValid(false);
        dto2.setPublishedOnly(false);
        dto2.setPage(2);
        dto2.setPageSize(30);
        dto2.setSortBy("title");
        dto2.setSortOrder("desc");
        
        assertEquals(100, dto2.getNovelId());
        assertEquals(10, dto2.getChapterNumber());
        assertEquals("title", dto2.getTitleKeyword()); // Should be trimmed
        assertTrue(dto2.getIsPremium());
        assertFalse(dto2.getIsValid());
        assertFalse(dto2.getPublishedOnly());
        assertEquals(2, dto2.getPage());
        assertEquals(30, dto2.getPageSize());
        assertEquals("title", dto2.getSortBy());
        assertEquals("desc", dto2.getSortOrder());
        assertEquals(30, dto2.getOffset()); // (2-1)*30 = 30
        
        // Test null titleKeyword
        dto2.setTitleKeyword(null);
        assertNull(dto2.getTitleKeyword());
        
        // Test equals, hashCode, and canEqual methods for ChapterDetailResponseDTO
        Date testDate = new Date();
        ChapterDetailResponseDTO detailDto1 = new ChapterDetailResponseDTO();
        detailDto1.setId(1);
        detailDto1.setNovelId(100);
        detailDto1.setTitle("Chapter 1");
        detailDto1.setPublishTime(testDate);
        
        ChapterDetailResponseDTO detailDto2 = new ChapterDetailResponseDTO();
        detailDto2.setId(1);
        detailDto2.setNovelId(100);
        detailDto2.setTitle("Chapter 1");
        detailDto2.setPublishTime(testDate);
        
        // Note: ChapterDetailResponseDTO and ChapterSearchRequestDTO don't have Lombok @Data,
        // so equals/hashCode are inherited from Object (reference equality).
        // Test basic object identity
        assertNotEquals(detailDto1, null);
        assertEquals(detailDto1, detailDto1);
        assertNotEquals(detailDto1, new Object());
        
        // Test ChapterSearchRequestDTO basic behavior
        ChapterSearchRequestDTO searchDto1 = new ChapterSearchRequestDTO();
        searchDto1.setNovelId(1);
        searchDto1.setPage(1);
        searchDto1.setPageSize(20);
        
        ChapterSearchRequestDTO searchDto2 = new ChapterSearchRequestDTO();
        searchDto2.setNovelId(1);
        searchDto2.setPage(1);
        searchDto2.setPageSize(20);
        
        assertNotEquals(searchDto1, null);
        assertEquals(searchDto1, searchDto1);
        assertNotEquals(searchDto1, new Object());
    }
}

