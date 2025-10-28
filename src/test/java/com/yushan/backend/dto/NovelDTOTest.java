package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Novel DTO Tests")
class NovelDTOTest {

    @Test
    @DisplayName("Test NovelRankDTO")
    void testNovelRankDTO() {
        NovelRankDTO dto = new NovelRankDTO();
        dto.setRank(1);
        dto.setNovelId(100);
        dto.setScore(4.5);
        dto.setRankType("daily");
        
        assertEquals(1, dto.getRank());
        assertEquals(100, dto.getNovelId());
        assertEquals(4.5, dto.getScore());
        assertEquals("daily", dto.getRankType());
    }

    @Test
    @DisplayName("Test NovelRankDTO with constructor")
    void testNovelRankDTOWithConstructor() {
        NovelRankDTO dto = new NovelRankDTO(100, 1L, 4.5, "daily");
        
        assertEquals(100, dto.getNovelId());
        assertEquals(1, dto.getRank());
        assertEquals(4.5, dto.getScore());
        assertEquals("daily", dto.getRankType());
        
        // Test equals, hashCode, and canEqual methods
        NovelRankDTO dto2 = new NovelRankDTO(100, 1L, 4.5, "daily");
        NovelRankDTO dto3 = new NovelRankDTO(100, 1L, 4.5, "daily");
        
        // Test equals - same values
        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        
        // Test equals - different values
        NovelRankDTO dto4 = new NovelRankDTO(200, 2L, 5.0, "weekly");
        assertNotEquals(dto2, dto4);
        
        // Test equals - null
        assertNotEquals(dto2, null);
        
        // Test equals - same object
        assertEquals(dto2, dto2);
        
        // Test equals - different class
        assertNotEquals(dto2, "not a DTO");
        
        // Test canEqual
        assertTrue(dto2.canEqual(dto3));
        assertFalse(dto2.canEqual("not a DTO"));
    }

    @Test
    @DisplayName("Test NovelRatingStatsDTO")
    void testNovelRatingStatsDTO() {
        NovelRatingStatsDTO dto = new NovelRatingStatsDTO();
        dto.setNovelId(1);
        dto.setNovelTitle("Test Novel");
        dto.setTotalReviews(100);
        dto.setAverageRating(4.5f);
        dto.setRating5Count(50);
        dto.setRating4Count(30);
        dto.setRating3Count(15);
        dto.setRating2Count(4);
        dto.setRating1Count(1);
        dto.setRating5Percentage(50.0f);
        dto.setRating4Percentage(30.0f);
        dto.setRating3Percentage(15.0f);
        dto.setRating2Percentage(4.0f);
        dto.setRating1Percentage(1.0f);
        
        assertEquals(1, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals(100, dto.getTotalReviews());
        assertEquals(4.5f, dto.getAverageRating());
        assertEquals(50, dto.getRating5Count());
        assertEquals(30, dto.getRating4Count());
        assertEquals(15, dto.getRating3Count());
        assertEquals(4, dto.getRating2Count());
        assertEquals(1, dto.getRating1Count());
        assertEquals(50.0f, dto.getRating5Percentage());
        assertEquals(30.0f, dto.getRating4Percentage());
        assertEquals(15.0f, dto.getRating3Percentage());
        assertEquals(4.0f, dto.getRating2Percentage());
        assertEquals(1.0f, dto.getRating1Percentage());
        
        // Test with zero/null values
        NovelRatingStatsDTO dto2 = new NovelRatingStatsDTO();
        dto2.setNovelId(2);
        dto2.setNovelTitle("New Novel");
        dto2.setTotalReviews(0);
        dto2.setAverageRating(null);
        dto2.setRating5Count(0);
        dto2.setRating4Count(0);
        dto2.setRating3Count(0);
        dto2.setRating2Count(0);
        dto2.setRating1Count(0);
        dto2.setRating5Percentage(0.0f);
        dto2.setRating4Percentage(0.0f);
        dto2.setRating3Percentage(0.0f);
        dto2.setRating2Percentage(0.0f);
        dto2.setRating1Percentage(0.0f);
        
        assertEquals(2, dto2.getNovelId());
        assertEquals("New Novel", dto2.getNovelTitle());
        assertEquals(0, dto2.getTotalReviews());
        assertNull(dto2.getAverageRating());
        assertEquals(0, dto2.getRating5Count());
        assertEquals(0, dto2.getRating4Count());
        assertEquals(0, dto2.getRating3Count());
        assertEquals(0, dto2.getRating2Count());
        assertEquals(0, dto2.getRating1Count());
        assertEquals(0.0f, dto2.getRating5Percentage());
        assertEquals(0.0f, dto2.getRating4Percentage());
        assertEquals(0.0f, dto2.getRating3Percentage());
        assertEquals(0.0f, dto2.getRating2Percentage());
        assertEquals(0.0f, dto2.getRating1Percentage());
        
        // Test with different values to cover all branches
        NovelRatingStatsDTO dto3 = new NovelRatingStatsDTO();
        dto3.setNovelId(999);
        dto3.setNovelTitle("Different Novel");
        dto3.setTotalReviews(200);
        dto3.setAverageRating(3.5f);
        dto3.setRating5Count(100);
        dto3.setRating4Count(50);
        dto3.setRating3Count(30);
        dto3.setRating2Count(15);
        dto3.setRating1Count(5);
        dto3.setRating5Percentage(50.0f);
        dto3.setRating4Percentage(25.0f);
        dto3.setRating3Percentage(15.0f);
        dto3.setRating2Percentage(7.5f);
        dto3.setRating1Percentage(2.5f);
        
        assertEquals(999, dto3.getNovelId());
        assertEquals("Different Novel", dto3.getNovelTitle());
        assertEquals(200, dto3.getTotalReviews());
        assertEquals(3.5f, dto3.getAverageRating());
        assertEquals(100, dto3.getRating5Count());
        assertEquals(50, dto3.getRating4Count());
        assertEquals(30, dto3.getRating3Count());
        assertEquals(15, dto3.getRating2Count());
        assertEquals(5, dto3.getRating1Count());
        assertEquals(50.0f, dto3.getRating5Percentage());
        assertEquals(25.0f, dto3.getRating4Percentage());
        assertEquals(15.0f, dto3.getRating3Percentage());
        assertEquals(7.5f, dto3.getRating2Percentage());
        assertEquals(2.5f, dto3.getRating1Percentage());
    }

    @Test
    @DisplayName("Test NovelSearchRequestDTO")
    void testNovelSearchRequestDTO() {
        NovelSearchRequestDTO dto = new NovelSearchRequestDTO();
        dto.setSearch("fantasy");
        dto.setCategoryId(1);
        dto.setStatus("PUBLISHED");
        dto.setAuthorName("Author");
        dto.setAuthorId(UUID.randomUUID().toString());
        dto.setPage(0);
        dto.setSize(20);
        dto.setSort("viewCnt");
        dto.setOrder("desc");
        
        assertEquals("fantasy", dto.getSearch());
        assertEquals(1, dto.getCategoryId());
        assertEquals("PUBLISHED", dto.getStatus());
        assertEquals("Author", dto.getAuthorName());
        assertNotNull(dto.getAuthorId());
        assertEquals(0, dto.getPage());
        assertEquals(20, dto.getSize());
        assertEquals("viewCnt", dto.getSort());
        assertEquals("desc", dto.getOrder());
        
        // Test helper methods
        assertTrue(dto.hasCategoryFilter());
        assertTrue(dto.hasStatusFilter());
        assertTrue(dto.hasSearchFilter());
        assertTrue(dto.hasAuthorFilter());
        assertTrue(dto.hasAuthorIdFilter());
        assertTrue(dto.isDescending());
        assertFalse(dto.isAscending());
        
        // Test all remaining helper methods and edge cases
        dto.setCategoryId(null);
        dto.setStatus(null);
        dto.setSearch(null);
        dto.setAuthorName(null);
        dto.setAuthorId(null);
        
        assertFalse(dto.hasCategoryFilter());
        assertFalse(dto.hasStatusFilter());
        assertFalse(dto.hasSearchFilter());
        assertFalse(dto.hasAuthorFilter());
        assertFalse(dto.hasAuthorIdFilter());
        
        // Test with empty strings
        dto.setStatus("   ");
        dto.setSearch("   ");
        dto.setAuthorName("   ");
        dto.setAuthorId("   ");
        
        assertFalse(dto.hasStatusFilter());
        assertFalse(dto.hasSearchFilter());
        assertFalse(dto.hasAuthorFilter());
        assertFalse(dto.hasAuthorIdFilter());
        
        // Test constructor with all params
        NovelSearchRequestDTO dto2 = new NovelSearchRequestDTO(
            5, 25, "title", "asc", 3, "DRAFT", "test", "author", "uuid"
        );
        assertEquals(5, dto2.getPage());
        assertEquals(25, dto2.getSize());
        assertEquals("title", dto2.getSort());
        assertEquals("asc", dto2.getOrder());
        assertEquals(3, dto2.getCategoryId());
        assertEquals("DRAFT", dto2.getStatus());
        assertEquals("test", dto2.getSearch());
        assertEquals("author", dto2.getAuthorName());
        assertEquals("uuid", dto2.getAuthorId());
        
        // Test constructor with null params (should use defaults)
        NovelSearchRequestDTO dto3 = new NovelSearchRequestDTO(
            null, null, null, null, null, null, null, null, null
        );
        assertEquals(0, dto3.getPage());
        assertEquals(10, dto3.getSize());
        assertEquals("createTime", dto3.getSort());
        assertEquals("desc", dto3.getOrder());
        
        // Test isAscending method with different cases
        NovelSearchRequestDTO dto4 = new NovelSearchRequestDTO();
        dto4.setOrder("ASC");
        assertTrue(dto4.isAscending());
        dto4.setOrder("Asc");
        assertTrue(dto4.isAscending());
        dto4.setOrder("aSc");
        assertTrue(dto4.isAscending());
        
        // Test isDescending method with different cases
        dto4.setOrder("DESC");
        assertTrue(dto4.isDescending());
        dto4.setOrder("Desc");
        assertTrue(dto4.isDescending());
        dto4.setOrder("dEsC");
        assertTrue(dto4.isDescending());
        
        // Test with invalid order values
        dto4.setOrder("invalid");
        assertFalse(dto4.isAscending());
        assertFalse(dto4.isDescending());
        
        // Test hasCategoryFilter with edge cases
        dto4.setCategoryId(0);
        assertFalse(dto4.hasCategoryFilter());
        dto4.setCategoryId(-1);
        assertFalse(dto4.hasCategoryFilter());
        dto4.setCategoryId(1);
        assertTrue(dto4.hasCategoryFilter());
        
        // Test equals, hashCode, and canEqual methods for NovelSearchRequestDTO
        NovelSearchRequestDTO searchDto1 = new NovelSearchRequestDTO();
        searchDto1.setSearch("Test");
        searchDto1.setCategoryId(1);
        searchDto1.setPage(0);
        searchDto1.setSize(20);
        
        NovelSearchRequestDTO searchDto2 = new NovelSearchRequestDTO();
        searchDto2.setSearch("Test");
        searchDto2.setCategoryId(1);
        searchDto2.setPage(0);
        searchDto2.setSize(20);
        
        assertEquals(searchDto1, searchDto2);
        assertEquals(searchDto1.hashCode(), searchDto2.hashCode());
        assertNotEquals(searchDto1, null);
        assertEquals(searchDto1, searchDto1);
        assertTrue(searchDto1.canEqual(searchDto2));
    }

    @Test
    @DisplayName("Test NovelDetailResponseDTO")
    void testNovelDetailResponseDTO() {
        Date now = new Date();
        UUID uuid = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        
        NovelDetailResponseDTO dto = new NovelDetailResponseDTO();
        dto.setId(1);
        dto.setUuid(uuid);
        dto.setTitle("Test Novel");
        dto.setSynopsis("Test synopsis");
        dto.setAuthorId(authorId);
        dto.setAuthorUsername("author");
        dto.setCategoryId(1);
        dto.setCategoryName("Fantasy");
        dto.setCoverImgUrl("http://cover.url");
        dto.setStatus("PUBLISHED");
        dto.setIsCompleted(false);
        dto.setChapterCnt(10);
        dto.setWordCnt(10000L);
        dto.setAvgRating(4.5f);
        dto.setReviewCnt(20);
        dto.setViewCnt(1000L);
        dto.setVoteCnt(100);
        dto.setYuanCnt(100.0f);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        dto.setPublishTime(now);
        
        assertEquals(1, dto.getId());
        assertEquals(uuid, dto.getUuid());
        assertEquals("Test Novel", dto.getTitle());
        assertEquals("Test synopsis", dto.getSynopsis());
        assertEquals(authorId, dto.getAuthorId());
        assertEquals("author", dto.getAuthorUsername());
        assertEquals(1, dto.getCategoryId());
        assertEquals("Fantasy", dto.getCategoryName());
        assertEquals("http://cover.url", dto.getCoverImgUrl());
        assertEquals("PUBLISHED", dto.getStatus());
        assertFalse(dto.getIsCompleted());
        assertEquals(10, dto.getChapterCnt());
        assertEquals(10000L, dto.getWordCnt());
        assertEquals(4.5f, dto.getAvgRating());
        assertEquals(20, dto.getReviewCnt());
        assertEquals(1000L, dto.getViewCnt());
        assertEquals(100, dto.getVoteCnt());
        assertEquals(100.0f, dto.getYuanCnt());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertNotNull(dto.getPublishTime());
        
        // Test defensive copying for Date fields
        Date testDate1 = new Date();
        dto.setCreateTime(testDate1);
        Date retrieved1 = dto.getCreateTime();
        assertNotSame(testDate1, retrieved1);
        testDate1.setTime(0);
        assertNotEquals(0, retrieved1.getTime());
        
        Date testDate2 = new Date();
        dto.setUpdateTime(testDate2);
        Date retrieved2 = dto.getUpdateTime();
        assertNotSame(testDate2, retrieved2);
        
        Date testDate3 = new Date();
        dto.setPublishTime(testDate3);
        Date retrieved3 = dto.getPublishTime();
        assertNotSame(testDate3, retrieved3);
        
        // Test with null Date values
        dto.setCreateTime(null);
        dto.setUpdateTime(null);
        dto.setPublishTime(null);
        assertNull(dto.getCreateTime());
        assertNull(dto.getUpdateTime());
        assertNull(dto.getPublishTime());
        
        // Test getPublishTime/getCreateTime/getUpdateTime with null check branches
        Date testPublishTime = new Date();
        dto.setPublishTime(testPublishTime);
        Date retrievedPublish = dto.getPublishTime();
        assertNotNull(retrievedPublish);
        assertNotSame(testPublishTime, retrievedPublish);
        
        Date testCreateTime = new Date();
        dto.setCreateTime(testCreateTime);
        Date retrievedCreate = dto.getCreateTime();
        assertNotNull(retrievedCreate);
        assertNotSame(testCreateTime, retrievedCreate);
        
        Date testUpdateTime = new Date();
        dto.setUpdateTime(testUpdateTime);
        Date retrievedUpdate = dto.getUpdateTime();
        assertNotNull(retrievedUpdate);
        assertNotSame(testUpdateTime, retrievedUpdate);
        
        // Test setPublishTime/setCreateTime/setUpdateTime with null parameter branches
        dto.setPublishTime(null);
        assertNull(dto.getPublishTime());
        
        dto.setCreateTime(null);
        assertNull(dto.getCreateTime());
        
        dto.setUpdateTime(null);
        assertNull(dto.getUpdateTime());
        
        // Test setPublishTime/setCreateTime/setUpdateTime with non-null parameter branches
        Date newPublishTime = new Date();
        dto.setPublishTime(newPublishTime);
        Date retrievedPublish2 = dto.getPublishTime();
        assertNotNull(retrievedPublish2);
        assertNotSame(newPublishTime, retrievedPublish2);
        
        Date newCreateTime = new Date();
        dto.setCreateTime(newCreateTime);
        Date retrievedCreate2 = dto.getCreateTime();
        assertNotNull(retrievedCreate2);
        assertNotSame(newCreateTime, retrievedCreate2);
        
        Date newUpdateTime = new Date();
        dto.setUpdateTime(newUpdateTime);
        Date retrievedUpdate2 = dto.getUpdateTime();
        assertNotNull(retrievedUpdate2);
        assertNotSame(newUpdateTime, retrievedUpdate2);
        
        // Test defensive copying - modify original date should not affect retrieved
        testPublishTime.setTime(0);
        assertNotEquals(0, retrievedPublish.getTime());
        
        testCreateTime.setTime(0);
        assertNotEquals(0, retrievedCreate.getTime());
        
        testUpdateTime.setTime(0);
        assertNotEquals(0, retrievedUpdate.getTime());
        
        // Test equals, hashCode, and canEqual methods for NovelDetailResponseDTO
        UUID uuid2 = UUID.randomUUID();
        NovelDetailResponseDTO dto2 = new NovelDetailResponseDTO();
        dto2.setId(1);
        dto2.setUuid(uuid2);
        dto2.setTitle("Novel");
        dto2.setCategoryId(1);
        dto2.setCreateTime(null);
        dto2.setUpdateTime(null);
        dto2.setPublishTime(null);
        
        NovelDetailResponseDTO dto3 = new NovelDetailResponseDTO();
        dto3.setId(1);
        dto3.setUuid(uuid2);
        dto3.setTitle("Novel");
        dto3.setCategoryId(1);
        dto3.setCreateTime(null);
        dto3.setUpdateTime(null);
        dto3.setPublishTime(null);
        
        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        assertNotEquals(dto2, null);
        assertEquals(dto2, dto2);
        assertTrue(dto2.canEqual(dto3));
        
        // Test all other fields with different values
        dto.setId(999);
        dto.setTitle("Different Novel");
        dto.setAvgRating(3.5f);
        dto.setReviewCnt(100);
        dto.setViewCnt(50000L);
        dto.setVoteCnt(1000);
        dto.setYuanCnt(1000.0f);
        assertEquals(999, dto.getId());
        assertEquals("Different Novel", dto.getTitle());
        assertEquals(3.5f, dto.getAvgRating());
        assertEquals(100, dto.getReviewCnt());
        assertEquals(50000L, dto.getViewCnt());
        assertEquals(1000, dto.getVoteCnt());
        assertEquals(1000.0f, dto.getYuanCnt());
        
        // Test all remaining fields
        dto.setUuid(UUID.randomUUID());
        dto.setTitle("Another Title");
        dto.setSynopsis("Another synopsis");
        dto.setAuthorId(UUID.randomUUID());
        dto.setAuthorUsername("differentAuthor");
        dto.setCategoryId(5);
        dto.setCategoryName("Mystery");
        dto.setCoverImgUrl("http://different.cover");
        dto.setStatus("DRAFT");
        dto.setIsCompleted(true);
        dto.setChapterCnt(50);
        
        assertNotNull(dto.getUuid());
        assertEquals("Another Title", dto.getTitle());
        assertEquals("Another synopsis", dto.getSynopsis());
        assertNotNull(dto.getAuthorId());
        assertEquals("differentAuthor", dto.getAuthorUsername());
        assertEquals(5, dto.getCategoryId());
        assertEquals("Mystery", dto.getCategoryName());
        assertEquals("http://different.cover", dto.getCoverImgUrl());
        assertEquals("DRAFT", dto.getStatus());
        assertTrue(dto.getIsCompleted());
        assertEquals(50, dto.getChapterCnt());
    }

    @Test
    @DisplayName("Test NovelCreateRequestDTO")
    void testNovelCreateRequestDTO() {
        NovelCreateRequestDTO dto = new NovelCreateRequestDTO();
        dto.setTitle("New Novel");
        dto.setSynopsis("Synopsis of the novel");
        dto.setCategoryId(1);
        dto.setCoverImgBase64("data:image/jpeg;base64,/9j/4AAQSkZJRg==");
        dto.setIsCompleted(false);
        
        assertEquals("New Novel", dto.getTitle());
        assertEquals("Synopsis of the novel", dto.getSynopsis());
        assertEquals(1, dto.getCategoryId());
        assertEquals("data:image/jpeg;base64,/9j/4AAQSkZJRg==", dto.getCoverImgBase64());
        assertFalse(dto.getIsCompleted());
        
        // Test with completed novel
        dto.setIsCompleted(true);
        assertTrue(dto.getIsCompleted());
        
        // Test with null isCompleted
        NovelCreateRequestDTO dto2 = new NovelCreateRequestDTO();
        dto2.setTitle("Title Without Completion");
        dto2.setCategoryId(1);
        dto2.setIsCompleted(null);
        assertEquals("Title Without Completion", dto2.getTitle());
        assertEquals(1, dto2.getCategoryId());
        assertNull(dto2.getIsCompleted());
        
        // Test with null coverImgBase64
        dto2.setCoverImgBase64(null);
        assertNull(dto2.getCoverImgBase64());
        
        // Test with different cover image formats
        dto2.setCoverImgBase64("data:image/jpeg;base64,/9j/4AAQSkZJRg==");
        assertEquals("data:image/jpeg;base64,/9j/4AAQSkZJRg==", dto2.getCoverImgBase64());
        
        dto2.setCoverImgBase64("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP");
        assertEquals("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP", dto2.getCoverImgBase64());
        
        // Test with long synopsis (max 4000)
        String longSynopsis = "B".repeat(4000);
        dto2.setSynopsis(longSynopsis);
        assertEquals(4000, dto2.getSynopsis().length());
        
        // Test with null synopsis
        dto2.setSynopsis(null);
        assertNull(dto2.getSynopsis());
    }

    @Test
    @DisplayName("Test NovelUpdateRequestDTO")
    void testNovelUpdateRequestDTO() {
        NovelUpdateRequestDTO dto = new NovelUpdateRequestDTO();
        dto.setTitle("Updated Title");
        dto.setSynopsis("Updated Synopsis");
        dto.setCategoryId(2);
        dto.setCoverImgBase64("data:image/png;base64,iVBORw0KGgo=");
        dto.setStatus("PUBLISHED");
        dto.setIsCompleted(true);
        
        assertEquals("Updated Title", dto.getTitle());
        assertEquals("Updated Synopsis", dto.getSynopsis());
        assertEquals(2, dto.getCategoryId());
        assertEquals("data:image/png;base64,iVBORw0KGgo=", dto.getCoverImgBase64());
        assertEquals("PUBLISHED", dto.getStatus());
        assertTrue(dto.getIsCompleted());
        
        // Test partial update
        NovelUpdateRequestDTO dto2 = new NovelUpdateRequestDTO();
        dto2.setTitle("Partial Update");
        assertEquals("Partial Update", dto2.getTitle());
        assertNull(dto2.getSynopsis());
        assertNull(dto2.getCategoryId());
        
        // Test all fields with null values for partial updates
        NovelUpdateRequestDTO dto3 = new NovelUpdateRequestDTO();
        dto3.setTitle(null);
        dto3.setSynopsis(null);
        dto3.setCategoryId(null);
        dto3.setCoverImgBase64(null);
        dto3.setStatus(null);
        dto3.setIsCompleted(null);
        
        assertNull(dto3.getTitle());
        assertNull(dto3.getSynopsis());
        assertNull(dto3.getCategoryId());
        assertNull(dto3.getCoverImgBase64());
        assertNull(dto3.getStatus());
        assertNull(dto3.getIsCompleted());
        
        // Test with different image formats
        dto3.setCoverImgBase64("data:image/png;base64,iVBORw0KGgo=");
        assertEquals("data:image/png;base64,iVBORw0KGgo=", dto3.getCoverImgBase64());
        
        dto3.setCoverImgBase64("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP==");
        assertEquals("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP==", dto3.getCoverImgBase64());
        
        dto3.setCoverImgBase64("data:image/webp;base64,UklGRiQAAABXRUJQVlA4");
        assertEquals("data:image/webp;base64,UklGRiQAAABXRUJQVlA4", dto3.getCoverImgBase64());
        
        // Test equals, hashCode, and canEqual methods
        NovelUpdateRequestDTO dto4 = new NovelUpdateRequestDTO();
        dto4.setTitle("Test");
        dto4.setCategoryId(1);
        
        NovelUpdateRequestDTO dto5 = new NovelUpdateRequestDTO();
        dto5.setTitle("Test");
        dto5.setCategoryId(1);
        
        // Test equals - same values
        assertEquals(dto4, dto5);
        assertEquals(dto4.hashCode(), dto5.hashCode());
        
        // Test equals - different values
        dto5.setTitle("Different");
        assertNotEquals(dto4, dto5);
        
        // Test equals - null
        assertNotEquals(dto4, null);
        
        // Test equals - same object
        assertEquals(dto4, dto4);
        
        // Test canEqual
        assertTrue(dto4.canEqual(dto5));
        
        // Test NovelCreateRequestDTO equals, hashCode, canEqual
        NovelCreateRequestDTO dto6 = new NovelCreateRequestDTO();
        dto6.setTitle("Test Novel");
        dto6.setCategoryId(1);
        
        NovelCreateRequestDTO dto7 = new NovelCreateRequestDTO();
        dto7.setTitle("Test Novel");
        dto7.setCategoryId(1);
        
        assertEquals(dto6, dto7);
        assertEquals(dto6.hashCode(), dto7.hashCode());
        assertNotEquals(dto6, null);
        assertEquals(dto6, dto6);
        assertTrue(dto6.canEqual(dto7));
        
        // Test NovelRatingStatsDTO equals, hashCode, canEqual
        NovelRatingStatsDTO dto8 = new NovelRatingStatsDTO();
        dto8.setNovelId(1);
        dto8.setNovelTitle("Novel");
        dto8.setTotalReviews(100);
        
        NovelRatingStatsDTO dto9 = new NovelRatingStatsDTO();
        dto9.setNovelId(1);
        dto9.setNovelTitle("Novel");
        dto9.setTotalReviews(100);
        
        assertEquals(dto8, dto9);
        assertEquals(dto8.hashCode(), dto9.hashCode());
        assertNotEquals(dto8, null);
        assertEquals(dto8, dto8);
        assertTrue(dto8.canEqual(dto9));
        
        // Test toString
        String toString = dto8.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("NovelRatingStatsDTO"));
    }
}

