package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Library and History DTO Tests")
class LibraryHistoryDTOTest {

    @Test
    @DisplayName("Test LibraryRequestDTO")
    void testLibraryRequestDTO() {
        LibraryRequestDTO dto = new LibraryRequestDTO();
        dto.setProgress(5);
        
        assertEquals(5, dto.getProgress());
        
        // Test with different values
        dto.setProgress(10);
        assertEquals(10, dto.getProgress());
        
        dto.setProgress(0);
        assertEquals(0, dto.getProgress());
        
        dto.setProgress(100);
        assertEquals(100, dto.getProgress());
    }

    @Test
    @DisplayName("Test LibraryResponseDTO")
    void testLibraryResponseDTO() {
        Date now = new Date();
        Date updated = new Date();
        
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setId(1);
        dto.setNovelId(100);
        dto.setNovelTitle("Test Novel");
        dto.setNovelAuthor("Test Author");
        dto.setNovelCover("http://cover.url");
        dto.setProgress(5);
        dto.setChapterNumber(5);
        dto.setChapterCnt(10);
        dto.setCreateTime(now);
        dto.setUpdateTime(updated);
        
        assertEquals(1, dto.getId());
        assertEquals(100, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals("Test Author", dto.getNovelAuthor());
        assertEquals("http://cover.url", dto.getNovelCover());
        assertEquals(5, dto.getProgress());
        assertEquals(5, dto.getChapterNumber());
        assertEquals(10, dto.getChapterCnt());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        
        // Test defensive copy for Date fields
        Date originalCreate = new Date();
        dto.setCreateTime(originalCreate);
        Date retrievedCreate = dto.getCreateTime();
        assertNotSame(originalCreate, retrievedCreate);
        
        originalCreate.setTime(0);
        assertNotEquals(originalCreate.getTime(), retrievedCreate.getTime());
        
        // Test all remaining fields with different values
        dto.setId(999);
        dto.setNovelId(888);
        dto.setNovelTitle("Different Novel");
        dto.setNovelAuthor("Different Author");
        dto.setNovelCover("http://different.cover");
        dto.setProgress(50);
        dto.setChapterNumber(100);
        dto.setChapterCnt(200);
        
        assertEquals(999, dto.getId());
        assertEquals(888, dto.getNovelId());
        assertEquals("Different Novel", dto.getNovelTitle());
        assertEquals("Different Author", dto.getNovelAuthor());
        assertEquals("http://different.cover", dto.getNovelCover());
        assertEquals(50, dto.getProgress());
        assertEquals(100, dto.getChapterNumber());
        assertEquals(200, dto.getChapterCnt());
        
        // Test null Date values
        dto.setCreateTime(null);
        dto.setUpdateTime(null);
        assertNull(dto.getCreateTime());
        assertNull(dto.getUpdateTime());
    }
    
    @Test
    @DisplayName("Test LibraryResponseDTO with constructor")
    void testLibraryResponseDTOWithConstructor() {
        Date now = new Date();
        Date updated = new Date();
        
        LibraryResponseDTO dto = new LibraryResponseDTO(
            1, 100, "Test Novel", "Test Author", "http://cover.url",
            5, 5, 10, now, updated
        );
        
        assertEquals(1, dto.getId());
        assertEquals(100, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals(5, dto.getProgress());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertNotSame(now, dto.getCreateTime());
        assertNotSame(updated, dto.getUpdateTime());
    }

    @Test
    @DisplayName("Test HistoryResponseDTO")
    void testHistoryResponseDTO() {
        Date now = new Date();
        
        HistoryResponseDTO dto = new HistoryResponseDTO();
        dto.setHistoryId(1);
        dto.setNovelId(10);
        dto.setNovelTitle("Test Novel");
        dto.setNovelCover("http://cover.url");
        dto.setCategoryId(2);
        dto.setCategoryName("Romance");
        dto.setAvgRating(4.5f);
        dto.setSynopsis("Test synopsis");
        dto.setChapterId(100);
        dto.setChapterNumber(15);
        dto.setChapterCnt(20);
        // isInLibrary không có setter vì là boolean primitive field với Lombok
        dto.setViewTime(now);
        
        assertEquals(1, dto.getHistoryId());
        assertEquals(10, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals("http://cover.url", dto.getNovelCover());
        assertEquals(2, dto.getCategoryId());
        assertEquals("Romance", dto.getCategoryName());
        assertEquals(4.5f, dto.getAvgRating());
        assertEquals("Test synopsis", dto.getSynopsis());
        assertEquals(100, dto.getChapterId());
        assertEquals(15, dto.getChapterNumber());
        assertEquals(20, dto.getChapterCnt());
        assertNotNull(dto.getViewTime());
        
        // Test defensive copy for Date field
        Date originalTime = new Date();
        dto.setViewTime(originalTime);
        Date retrievedTime = dto.getViewTime();
        assertNotSame(originalTime, retrievedTime);
        
        originalTime.setTime(0);
        assertNotEquals(originalTime.getTime(), retrievedTime.getTime());
        
        // Test null Date value
        dto.setViewTime(null);
        assertNull(dto.getViewTime());
        
        // Test all other fields with different values
        dto.setHistoryId(999);
        dto.setNovelId(888);
        dto.setNovelTitle("Different Novel");
        dto.setNovelCover("http://different.cover");
        dto.setCategoryId(7);
        dto.setCategoryName("Horror");
        dto.setAvgRating(3.0f);
        dto.setSynopsis("Different synopsis");
        dto.setChapterId(555);
        dto.setChapterNumber(99);
        dto.setChapterCnt(150);
        
        assertEquals(999, dto.getHistoryId());
        assertEquals(888, dto.getNovelId());
        assertEquals("Different Novel", dto.getNovelTitle());
        assertEquals("http://different.cover", dto.getNovelCover());
        assertEquals(7, dto.getCategoryId());
        assertEquals("Horror", dto.getCategoryName());
        assertEquals(3.0f, dto.getAvgRating());
        assertEquals("Different synopsis", dto.getSynopsis());
        assertEquals(555, dto.getChapterId());
        assertEquals(99, dto.getChapterNumber());
        assertEquals(150, dto.getChapterCnt());
        
        // Test equals, hashCode, and canEqual methods
        LibraryRequestDTO lib1 = new LibraryRequestDTO();
        lib1.setProgress(5);
        
        LibraryRequestDTO lib2 = new LibraryRequestDTO();
        lib2.setProgress(5);
        
        assertEquals(lib1, lib2);
        assertEquals(lib1.hashCode(), lib2.hashCode());
        assertNotEquals(lib1, null);
        assertEquals(lib1, lib1);
        assertTrue(lib1.canEqual(lib2));
        
        // Test LibraryResponseDTO equals, hashCode, canEqual
        Date testDate = new Date();
        LibraryResponseDTO libResp1 = new LibraryResponseDTO();
        libResp1.setNovelId(1);
        libResp1.setProgress(10);
        libResp1.setCreateTime(testDate);
        
        LibraryResponseDTO libResp2 = new LibraryResponseDTO();
        libResp2.setNovelId(1);
        libResp2.setProgress(10);
        libResp2.setCreateTime(testDate);
        
        assertEquals(libResp1, libResp2);
        assertEquals(libResp1.hashCode(), libResp2.hashCode());
        assertNotEquals(libResp1, null);
        assertEquals(libResp1, libResp1);
        assertTrue(libResp1.canEqual(libResp2));
        
        // Test HistoryResponseDTO equals, hashCode, canEqual
        HistoryResponseDTO hist1 = new HistoryResponseDTO();
        hist1.setHistoryId(1);
        hist1.setNovelId(100);
        hist1.setViewTime(testDate);
        
        HistoryResponseDTO hist2 = new HistoryResponseDTO();
        hist2.setHistoryId(1);
        hist2.setNovelId(100);
        hist2.setViewTime(testDate);
        
        assertEquals(hist1, hist2);
        assertEquals(hist1.hashCode(), hist2.hashCode());
        assertNotEquals(hist1, null);
        assertEquals(hist1, hist1);
        assertTrue(hist1.canEqual(hist2));
    }
}

