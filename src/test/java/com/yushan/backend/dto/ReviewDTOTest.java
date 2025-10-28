package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Review DTO Tests")
class ReviewDTOTest {

    @Test
    @DisplayName("Test ReviewCreateRequestDTO")
    void testReviewCreateRequestDTO() {
        ReviewCreateRequestDTO dto = new ReviewCreateRequestDTO();
        dto.setNovelId(1);
        dto.setRating(5);
        dto.setTitle("Great novel!");
        dto.setContent("This is an excellent story.");
        dto.setIsSpoiler(false);
        
        assertEquals(1, dto.getNovelId());
        assertEquals(5, dto.getRating());
        assertEquals("Great novel!", dto.getTitle());
        assertEquals("This is an excellent story.", dto.getContent());
        assertFalse(dto.getIsSpoiler());
        
        // Test default isSpoiler value
        ReviewCreateRequestDTO dto2 = new ReviewCreateRequestDTO();
        dto2.setNovelId(2);
        dto2.setRating(4);
        dto2.setTitle("Good novel");
        dto2.setContent("Nice story");
        assertEquals(2, dto2.getNovelId());
        assertEquals(4, dto2.getRating());
        assertEquals("Good novel", dto2.getTitle());
        assertEquals("Nice story", dto2.getContent());
        assertFalse(dto2.getIsSpoiler()); // Default should be false
        
        // Test with spoiler
        dto2.setIsSpoiler(true);
        assertTrue(dto2.getIsSpoiler());
        
        // Test equals, hashCode, and canEqual methods
        ReviewCreateRequestDTO dto3 = new ReviewCreateRequestDTO();
        dto3.setNovelId(1);
        dto3.setRating(5);
        dto3.setTitle("Title");
        dto3.setContent("Content");
        
        ReviewCreateRequestDTO dto4 = new ReviewCreateRequestDTO();
        dto4.setNovelId(1);
        dto4.setRating(5);
        dto4.setTitle("Title");
        dto4.setContent("Content");
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
    }

    @Test
    @DisplayName("Test ReviewUpdateRequestDTO")
    void testReviewUpdateRequestDTO() {
        ReviewUpdateRequestDTO dto = new ReviewUpdateRequestDTO();
        dto.setRating(4);
        dto.setTitle("Good novel");
        dto.setContent("Updated review content");
        dto.setIsSpoiler(true);
        
        assertEquals(4, dto.getRating());
        assertEquals("Good novel", dto.getTitle());
        assertEquals("Updated review content", dto.getContent());
        assertTrue(dto.getIsSpoiler());
        
        // Test partial update
        ReviewUpdateRequestDTO dto2 = new ReviewUpdateRequestDTO();
        dto2.setRating(3);
        assertEquals(3, dto2.getRating());
        assertNull(dto2.getTitle());
        assertNull(dto2.getContent());
        assertNull(dto2.getIsSpoiler());
        
        // Test null values
        dto2.setTitle(null);
        dto2.setContent(null);
        dto2.setIsSpoiler(null);
        assertNull(dto2.getTitle());
        assertNull(dto2.getContent());
        assertNull(dto2.getIsSpoiler());
        
        // Test equals, hashCode, and canEqual methods
        ReviewUpdateRequestDTO dto3 = new ReviewUpdateRequestDTO();
        dto3.setTitle("Updated");
        dto3.setContent("Content");
        
        ReviewUpdateRequestDTO dto4 = new ReviewUpdateRequestDTO();
        dto4.setTitle("Updated");
        dto4.setContent("Content");
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
        
        // Test ReviewSearchRequestDTO equals, hashCode, canEqual
        ReviewSearchRequestDTO dto5 = new ReviewSearchRequestDTO();
        dto5.setNovelId(1);
        dto5.setPage(0);
        dto5.setSize(20);
        
        ReviewSearchRequestDTO dto6 = new ReviewSearchRequestDTO();
        dto6.setNovelId(1);
        dto6.setPage(0);
        dto6.setSize(20);
        
        assertEquals(dto5, dto6);
        assertEquals(dto5.hashCode(), dto6.hashCode());
        assertNotEquals(dto5, null);
        assertEquals(dto5, dto5);
        assertTrue(dto5.canEqual(dto6));
    }

    @Test
    @DisplayName("Test ReviewSearchRequestDTO")
    void testReviewSearchRequestDTO() {
        ReviewSearchRequestDTO dto = new ReviewSearchRequestDTO();
        dto.setPage(0);
        dto.setSize(10);
        dto.setSort("createTime");
        dto.setOrder("desc");
        dto.setNovelId(1);
        dto.setRating(5);
        dto.setIsSpoiler(false);
        dto.setSearch("great");
        
        assertEquals(0, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("createTime", dto.getSort());
        assertEquals("desc", dto.getOrder());
        assertEquals(1, dto.getNovelId());
        assertEquals(5, dto.getRating());
        assertFalse(dto.getIsSpoiler());
        assertEquals("great", dto.getSearch());
    }

    @Test
    @DisplayName("Test ReviewSearchRequestDTO with all parameters")
    void testReviewSearchRequestDTOAllParams() {
        ReviewSearchRequestDTO dto = new ReviewSearchRequestDTO(
            1, 20, "rating", "asc", 100, 4, true, "story"
        );
        
        assertEquals(1, dto.getPage());
        assertEquals(20, dto.getSize());
        assertEquals("rating", dto.getSort());
        assertEquals("asc", dto.getOrder());
        assertEquals(100, dto.getNovelId());
        assertEquals(4, dto.getRating());
        assertTrue(dto.getIsSpoiler());
        assertEquals("story", dto.getSearch());
    }

    @Test
    @DisplayName("Test ReviewSearchRequestDTO with default values")
    void testReviewSearchRequestDTODefaults() {
        ReviewSearchRequestDTO dto = new ReviewSearchRequestDTO();
        
        assertEquals(0, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals("createTime", dto.getSort());
        assertEquals("desc", dto.getOrder());
        assertNull(dto.getNovelId());
        assertNull(dto.getRating());
        assertNull(dto.getIsSpoiler());
        assertNull(dto.getSearch());
    }

    @Test
    @DisplayName("Test ReviewResponseDTO")
    void testReviewResponseDTO() {
        java.util.Date now = new java.util.Date();
        java.util.UUID uuid = java.util.UUID.randomUUID();
        java.util.UUID userId = java.util.UUID.randomUUID();
        
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(1);
        dto.setUuid(uuid);
        dto.setUserId(userId);
        dto.setUsername("reviewer");
        dto.setNovelId(100);
        dto.setNovelTitle("Test Novel");
        dto.setRating(5);
        dto.setTitle("Great Review");
        dto.setContent("This is excellent!");
        dto.setLikeCnt(10);
        dto.setIsSpoiler(false);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        
        assertEquals(1, dto.getId());
        assertEquals(uuid, dto.getUuid());
        assertEquals(userId, dto.getUserId());
        assertEquals("reviewer", dto.getUsername());
        assertEquals(100, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals(5, dto.getRating());
        assertEquals("Great Review", dto.getTitle());
        assertEquals("This is excellent!", dto.getContent());
        assertEquals(10, dto.getLikeCnt());
        assertFalse(dto.getIsSpoiler());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        
        // Test defensive copying
        java.util.Date date = new java.util.Date();
        dto.setCreateTime(date);
        java.util.Date retrieved = dto.getCreateTime();
        assertNotSame(date, retrieved);
        date.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        // Test all remaining fields with different values
        UUID uuid2 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        
        dto.setId(999);
        dto.setUuid(uuid2);
        dto.setUserId(userId2);
        dto.setUsername("differentUser");
        dto.setNovelId(888);
        dto.setNovelTitle("Different Novel");
        dto.setRating(1);
        dto.setTitle("Different Title");
        dto.setContent("Different Content");
        dto.setLikeCnt(100);
        dto.setIsSpoiler(true);
        
        assertEquals(999, dto.getId());
        assertEquals(uuid2, dto.getUuid());
        assertEquals(userId2, dto.getUserId());
        assertEquals("differentUser", dto.getUsername());
        assertEquals(888, dto.getNovelId());
        assertEquals("Different Novel", dto.getNovelTitle());
        assertEquals(1, dto.getRating());
        assertEquals("Different Title", dto.getTitle());
        assertEquals("Different Content", dto.getContent());
        assertEquals(100, dto.getLikeCnt());
        assertTrue(dto.getIsSpoiler());
        
        // Test null Date values
        dto.setCreateTime(null);
        dto.setUpdateTime(null);
        assertNull(dto.getCreateTime());
        assertNull(dto.getUpdateTime());
        
        // Test all Date getters/setters with null check branches for complete coverage
        Date testCreateTime = new Date();
        dto.setCreateTime(null);
        assertNull(dto.getCreateTime());
        
        dto.setCreateTime(testCreateTime);
        Date retrievedCreateTime = dto.getCreateTime();
        assertNotNull(retrievedCreateTime);
        assertNotSame(testCreateTime, retrievedCreateTime);
        
        Date testUpdateTime = new Date();
        dto.setUpdateTime(null);
        assertNull(dto.getUpdateTime());
        
        dto.setUpdateTime(testUpdateTime);
        Date retrievedUpdateTime = dto.getUpdateTime();
        assertNotNull(retrievedUpdateTime);
        assertNotSame(testUpdateTime, retrievedUpdateTime);
        
        // Test defensive copying - modify original date should not affect retrieved
        testCreateTime.setTime(0);
        assertNotEquals(0, retrievedCreateTime.getTime());
        
        testUpdateTime.setTime(0);
        assertNotEquals(0, retrievedUpdateTime.getTime());
        
        // Test equals, hashCode, and canEqual methods
        ReviewResponseDTO dto2 = new ReviewResponseDTO();
        dto2.setId(1);
        dto2.setNovelId(100);
        dto2.setRating(5);
        
        ReviewResponseDTO dto3 = new ReviewResponseDTO();
        dto3.setId(1);
        dto3.setNovelId(100);
        dto3.setRating(5);
        
        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        assertNotEquals(dto2, null);
        assertEquals(dto2, dto2);
        assertTrue(dto2.canEqual(dto3));
    }
}

