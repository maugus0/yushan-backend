package com.yushan.backend.service;

import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Chapter;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ChapterService with current method signatures and UUID handling.
 */
public class ChapterServiceTest {

    private ChapterMapper chapterMapper;
    private NovelMapper novelMapper;
    private ChapterService chapterService;

    @BeforeEach
    void setUp() {
        chapterMapper = Mockito.mock(ChapterMapper.class);
        novelMapper = Mockito.mock(NovelMapper.class);

        chapterService = new ChapterService();
        try {
            Field chapterMapperField = ChapterService.class.getDeclaredField("chapterMapper");
            chapterMapperField.setAccessible(true);
            chapterMapperField.set(chapterService, chapterMapper);

            Field novelMapperField = ChapterService.class.getDeclaredField("novelMapper");
            novelMapperField.setAccessible(true);
            novelMapperField.set(chapterService, novelMapper);
        } catch (Exception e) {
            fail("Failed to set up test dependencies: " + e.getMessage());
        }
    }

    @Test
    void createChapter_ShouldInsertWithDefaults_AndReturnDto() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ChapterCreateRequestDTO req = new ChapterCreateRequestDTO();
        req.setNovelId(1);
        req.setChapterNumber(1);
        req.setTitle("Chapter 1");
        req.setContent("This is chapter content");
        req.setIsPremium(false);
        req.setYuanCost(0.0f);

        Novel novel = createTestNovel(1, userId);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        when(chapterMapper.existsByNovelIdAndChapterNumber(1, 1)).thenReturn(false);
        when(chapterMapper.insertSelective(any(Chapter.class))).thenReturn(1);
        
        // Mock updateNovelStatistics dependencies
        when(chapterMapper.countByNovelId(1)).thenReturn(1L);
        when(chapterMapper.sumWordCountByNovelId(1)).thenReturn(100L);
        when(novelMapper.updateByPrimaryKeySelective(any())).thenReturn(1);

        Chapter createdChapter = createTestChapter(UUID.randomUUID(), 1, 1);
        createdChapter.setTitle("Chapter 1"); // Match the expected title
        when(chapterMapper.selectByUuid(any(UUID.class))).thenReturn(createdChapter);

        // Act
        ChapterDetailResponseDTO response = chapterService.createChapter(userId, req);

        // Assert interactions - novelMapper.selectByPrimaryKey is called twice (once for validation, once for updateNovelStatistics)
        verify(novelMapper, times(2)).selectByPrimaryKey(1);
        verify(chapterMapper, times(1)).existsByNovelIdAndChapterNumber(1, 1);
        verify(chapterMapper, times(1)).insertSelective(argThat(chapter -> {
            return chapter.getNovelId().equals(1) &&
                   chapter.getChapterNumber().equals(1) &&
                   chapter.getTitle().equals("Chapter 1") &&
                   chapter.getContent().equals("This is chapter content") &&
                   chapter.getIsPremium().equals(false) &&
                   chapter.getYuanCost().equals(0.0f) &&
                   chapter.getViewCnt().equals(0L) &&
                   chapter.getIsValid().equals(true);
        }));

        assertNotNull(response);
        assertEquals(Integer.valueOf(1), response.getChapterNumber());
        assertEquals("Chapter 1", response.getTitle());
    }

    @Test
    void createChapter_NovelNotFound_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        ChapterCreateRequestDTO req = new ChapterCreateRequestDTO();
        req.setNovelId(999);
        req.setChapterNumber(1);
        req.setTitle("Chapter 1");

        when(novelMapper.selectByPrimaryKey(999)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> chapterService.createChapter(userId, req));
        verify(chapterMapper, never()).insertSelective(any());
    }

    @Test
    void createChapter_NovelInvalid_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        ChapterCreateRequestDTO req = new ChapterCreateRequestDTO();
        req.setNovelId(1);
        req.setChapterNumber(1);
        req.setTitle("Chapter 1");

        Novel invalidNovel = createTestNovel(1, userId);
        invalidNovel.setIsValid(false);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(invalidNovel);

        assertThrows(ResourceNotFoundException.class, () -> chapterService.createChapter(userId, req));
        verify(chapterMapper, never()).insertSelective(any());
    }

    @Test
    void createChapter_NotAuthor_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        UUID differentAuthorId = UUID.randomUUID();
        ChapterCreateRequestDTO req = new ChapterCreateRequestDTO();
        req.setNovelId(1);
        req.setChapterNumber(1);
        req.setTitle("Chapter 1");

        Novel novel = createTestNovel(1, differentAuthorId);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);

        assertThrows(IllegalArgumentException.class, () -> chapterService.createChapter(userId, req));
        verify(chapterMapper, never()).insertSelective(any());
    }

    @Test
    void createChapter_ChapterNumberExists_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        ChapterCreateRequestDTO req = new ChapterCreateRequestDTO();
        req.setNovelId(1);
        req.setChapterNumber(1);
        req.setTitle("Chapter 1");

        Novel novel = createTestNovel(1, userId);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        when(chapterMapper.existsByNovelIdAndChapterNumber(1, 1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> chapterService.createChapter(userId, req));
        verify(chapterMapper, never()).insertSelective(any());
    }

    @Test
    void getChapterByUuid_Valid_ShouldReturnResponse() {
        UUID chapterUuid = UUID.randomUUID();
        Chapter chapter = createTestChapter(chapterUuid, 1, 1);
        chapter.setTitle("Test Chapter");
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(chapter);
        when(chapterMapper.selectNextChapter(1, 1)).thenReturn(null);
        when(chapterMapper.selectPreviousChapter(1, 1)).thenReturn(null);

        ChapterDetailResponseDTO response = chapterService.getChapterByUuid(chapterUuid);

        assertNotNull(response);
        assertEquals("Test Chapter", response.getTitle());
        assertEquals(Integer.valueOf(1), response.getChapterNumber());
    }

    @Test
    void getChapterByUuid_NotFound_ShouldThrow() {
        UUID chapterUuid = UUID.randomUUID();
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> chapterService.getChapterByUuid(chapterUuid));
    }

    @Test
    void getChapterByUuid_Invalid_ShouldThrow() {
        UUID chapterUuid = UUID.randomUUID();
        Chapter invalidChapter = createTestChapter(chapterUuid, 1, 1);
        invalidChapter.setIsValid(false);
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(invalidChapter);

        assertThrows(ResourceNotFoundException.class, () -> chapterService.getChapterByUuid(chapterUuid));
    }

    @Test
    void getChaptersByNovelId_ShouldReturnPaginatedResults() {
        Integer novelId = 1;
        Novel novel = createTestNovel(novelId, UUID.randomUUID());
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);

        Chapter chapter1 = createTestChapter(UUID.randomUUID(), novelId, 1);
        Chapter chapter2 = createTestChapter(UUID.randomUUID(), novelId, 2);
        List<Chapter> chapters = Arrays.asList(chapter1, chapter2);

        when(chapterMapper.selectPublishedByNovelIdWithPagination(novelId, 0, 20)).thenReturn(chapters);
        when(chapterMapper.countPublishedByNovelId(novelId)).thenReturn(2L);

        ChapterListResponseDTO response = chapterService.getChaptersByNovelId(novelId, 1, 20, true);

        assertNotNull(response);
        assertEquals(2, response.getChapters().size());
        assertEquals(2L, response.getTotalCount());
        assertEquals(Integer.valueOf(1), response.getCurrentPage());
        assertEquals(Integer.valueOf(20), response.getPageSize());
        assertEquals(Integer.valueOf(1), response.getTotalPages());
    }

    @Test
    void getChaptersByNovelId_NovelNotFound_ShouldThrow() {
        Integer novelId = 999;
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> 
            chapterService.getChaptersByNovelId(novelId, 1, 20, true));
    }

    @Test
    void getChaptersByNovelId_WithDefaultParameters() {
        Integer novelId = 1;
        Novel novel = createTestNovel(novelId, UUID.randomUUID());
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
        when(chapterMapper.selectPublishedByNovelIdWithPagination(novelId, 0, 20)).thenReturn(Arrays.asList());
        when(chapterMapper.countPublishedByNovelId(novelId)).thenReturn(0L);

        ChapterListResponseDTO response = chapterService.getChaptersByNovelId(novelId, null, null, true);

        assertNotNull(response);
        assertEquals(Integer.valueOf(1), response.getCurrentPage());
        assertEquals(Integer.valueOf(20), response.getPageSize());
    }

    @Test
    void getChaptersByNovelId_ShouldLimitMaxPageSize() {
        Integer novelId = 1;
        Novel novel = createTestNovel(novelId, UUID.randomUUID());
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
        when(chapterMapper.selectPublishedByNovelIdWithPagination(novelId, 0, 100)).thenReturn(Arrays.asList());
        when(chapterMapper.countPublishedByNovelId(novelId)).thenReturn(0L);

        ChapterListResponseDTO response = chapterService.getChaptersByNovelId(novelId, 1, 200, true);

        assertNotNull(response);
        assertEquals(Integer.valueOf(100), response.getPageSize());
    }

    @Test
    void updateChapter_ShouldUpdateSelective() {
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();
        ChapterUpdateRequestDTO req = new ChapterUpdateRequestDTO();
        req.setUuid(chapterUuid);
        req.setTitle("Updated Title");
        req.setContent("Updated content");

        Chapter existing = createTestChapter(chapterUuid, 1, 1);
        Novel novel = createTestNovel(1, userId);
        
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(existing);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        when(chapterMapper.updateByPrimaryKeySelective(any(Chapter.class))).thenReturn(1);

        // Mock the second call for returning updated chapter
        Chapter updatedChapter = createTestChapter(chapterUuid, 1, 1);
        updatedChapter.setTitle("Updated Title");
        updatedChapter.setContent("Updated content");
        when(chapterMapper.selectByUuid(chapterUuid))
            .thenReturn(existing)
            .thenReturn(updatedChapter);
        when(chapterMapper.selectNextChapter(1, 1)).thenReturn(null);
        when(chapterMapper.selectPreviousChapter(1, 1)).thenReturn(null);

        ChapterDetailResponseDTO response = chapterService.updateChapter(userId, req);

        verify(chapterMapper, times(1)).updateByPrimaryKeySelective(argThat(chapter -> 
            chapter.getTitle().equals("Updated Title") && 
            chapter.getContent().equals("Updated content")
        ));

        assertNotNull(response);
    }

    @Test
    void updateChapter_ChapterNotFound_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();
        ChapterUpdateRequestDTO req = new ChapterUpdateRequestDTO();
        req.setUuid(chapterUuid);

        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> chapterService.updateChapter(userId, req));
        verify(chapterMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void updateChapter_NotAuthor_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        UUID differentAuthorId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();
        ChapterUpdateRequestDTO req = new ChapterUpdateRequestDTO();
        req.setUuid(chapterUuid);

        Chapter existing = createTestChapter(chapterUuid, 1, 1);
        Novel novel = createTestNovel(1, differentAuthorId);
        
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(existing);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);

        assertThrows(IllegalArgumentException.class, () -> chapterService.updateChapter(userId, req));
        verify(chapterMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void deleteChapter_ShouldSoftDelete() {
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();

        Chapter existing = createTestChapter(chapterUuid, 1, 1);
        Novel novel = createTestNovel(1, userId);
        
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(existing);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        when(chapterMapper.softDeleteByUuid(chapterUuid)).thenReturn(1);
        when(chapterMapper.countByNovelId(1)).thenReturn(5L);
        when(chapterMapper.sumWordCountByNovelId(1)).thenReturn(1000L);
        when(novelMapper.updateByPrimaryKeySelective(any())).thenReturn(1);

        chapterService.deleteChapter(userId, chapterUuid);

        verify(chapterMapper, times(1)).softDeleteByUuid(chapterUuid);
        verify(chapterMapper, times(1)).countByNovelId(1);
        verify(chapterMapper, times(1)).sumWordCountByNovelId(1);
        verify(novelMapper, times(1)).updateByPrimaryKeySelective(any());
    }

    @Test
    void deleteChapter_ChapterNotFound_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();

        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> chapterService.deleteChapter(userId, chapterUuid));
        verify(chapterMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void deleteChapter_NotAuthor_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        UUID differentAuthorId = UUID.randomUUID();
        UUID chapterUuid = UUID.randomUUID();

        Chapter existing = createTestChapter(chapterUuid, 1, 1);
        Novel novel = createTestNovel(1, differentAuthorId);
        
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(existing);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);

        assertThrows(IllegalArgumentException.class, () -> chapterService.deleteChapter(userId, chapterUuid));
        verify(chapterMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void getChapterStatistics_ShouldReturnStats() {
        Integer novelId = 1;
        Novel novel = createTestNovel(novelId, UUID.randomUUID());
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);

        when(chapterMapper.countByNovelId(novelId)).thenReturn(10L);
        when(chapterMapper.countPublishedByNovelId(novelId)).thenReturn(8L);
        when(chapterMapper.selectDraftsByNovelId(novelId)).thenReturn(Arrays.asList(new Chapter()));
        when(chapterMapper.selectScheduledByNovelId(novelId)).thenReturn(Arrays.asList(new Chapter()));

        Chapter chapter1 = createTestChapter(UUID.randomUUID(), novelId, 1);
        chapter1.setIsPremium(true);
        chapter1.setViewCnt(100L);
        chapter1.setYuanCost(1.0f);
        Chapter chapter2 = createTestChapter(UUID.randomUUID(), novelId, 2);
        chapter2.setIsPremium(false);
        chapter2.setViewCnt(50L);
        List<Chapter> allChapters = Arrays.asList(chapter1, chapter2);

        when(chapterMapper.selectByNovelId(novelId)).thenReturn(allChapters);
        when(chapterMapper.sumWordCountByNovelId(novelId)).thenReturn(5000L);
        when(chapterMapper.selectMaxChapterNumberByNovelId(novelId)).thenReturn(10);

        ChapterStatisticsResponseDTO response = chapterService.getChapterStatistics(novelId);

        assertNotNull(response);
        assertEquals(10L, response.getTotalChapters());
        assertEquals(8L, response.getPublishedChapters());
        assertEquals(1L, response.getDraftChapters());
        assertEquals(1L, response.getScheduledChapters());
        assertEquals(1L, response.getPremiumChapters());
        assertEquals(9L, response.getFreeChapters());
        assertEquals(5000L, response.getTotalWordCount());
        assertEquals(150L, response.getTotalViewCount());
        assertEquals(100.0f, response.getTotalRevenue());
        assertEquals(Integer.valueOf(10), response.getMaxChapterNumber());
    }

    @Test
    void incrementViewCount_ShouldIncrementCount() {
        UUID chapterUuid = UUID.randomUUID();
        Chapter chapter = createTestChapter(chapterUuid, 1, 1);
        chapter.setId(123); // Set an ID for the chapter
        when(chapterMapper.selectByUuid(chapterUuid)).thenReturn(chapter);
        when(chapterMapper.incrementViewCount(123)).thenReturn(1);

        chapterService.incrementViewCount(chapterUuid);

        verify(chapterMapper, times(1)).incrementViewCount(123);
    }

    @Test
    void chapterExists_ShouldReturnCorrectValue() {
        when(chapterMapper.existsByNovelIdAndChapterNumber(1, 1)).thenReturn(true);
        when(chapterMapper.existsByNovelIdAndChapterNumber(1, 2)).thenReturn(false);

        assertTrue(chapterService.chapterExists(1, 1));
        assertFalse(chapterService.chapterExists(1, 2));
    }

    @Test
    void getNextAvailableChapterNumber_ShouldReturnNextNumber() {
        when(chapterMapper.selectMaxChapterNumberByNovelId(1)).thenReturn(5);

        Integer nextNumber = chapterService.getNextAvailableChapterNumber(1);

        assertEquals(Integer.valueOf(6), nextNumber);
    }

    @Test
    void getNextAvailableChapterNumber_NoChapters_ShouldReturn1() {
        when(chapterMapper.selectMaxChapterNumberByNovelId(1)).thenReturn(null);

        Integer nextNumber = chapterService.getNextAvailableChapterNumber(1);

        assertEquals(Integer.valueOf(1), nextNumber);
    }

    // Helper methods
    private Novel createTestNovel(Integer id, UUID authorId) {
        Novel novel = new Novel();
        novel.setId(id);
        novel.setAuthorId(authorId);
        novel.setTitle("Test Novel");
        novel.setIsValid(true);
        novel.setStatus(1); // PUBLISHED
        novel.setCreateTime(new Date());
        novel.setUpdateTime(new Date());
        return novel;
    }

    private Chapter createTestChapter(UUID uuid, Integer novelId, Integer chapterNumber) {
        Chapter chapter = new Chapter();
        chapter.setId(1); // Add ID for database operations
        chapter.setUuid(uuid);
        chapter.setNovelId(novelId);
        chapter.setChapterNumber(chapterNumber);
        chapter.setTitle("Test Chapter " + chapterNumber);
        chapter.setContent("Test content");
        chapter.setWordCnt(100);
        chapter.setIsPremium(false);
        chapter.setYuanCost(0.0f);
        chapter.setViewCnt(0L);
        chapter.setIsValid(true);
        chapter.setCreateTime(new Date());
        chapter.setUpdateTime(new Date());
        chapter.setPublishTime(new Date());
        return chapter;
    }
}
