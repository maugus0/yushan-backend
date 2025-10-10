package com.yushan.backend.service;

import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.dao.HistoryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.HistoryResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.entity.Category;
import com.yushan.backend.entity.Chapter;
import com.yushan.backend.entity.History;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryMapper historyMapper;
    @Mock
    private NovelMapper novelMapper;
    @Mock
    private ChapterMapper chapterMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private HistoryService historyService;

    private UUID userId;
    private Integer novelId;
    private Integer chapterId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        novelId = 1;
        chapterId = 101;
    }

    @Nested
    @DisplayName("addOrUpdateHistory Tests")
    class AddOrUpdateHistory {
        @Test
        @DisplayName("Should create a new history record when one does not exist for the novel")
        void shouldCreateNewRecord_whenHistoryDoesNotExist() {
            // Given: Novel and Chapter exist, and chapter belongs to the novel
            Novel novel = new Novel();
            novel.setId(novelId);

            Chapter chapter = new Chapter();
            chapter.setId(chapterId);
            chapter.setNovelId(novelId);

            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
            when(chapterMapper.selectByPrimaryKey(chapterId)).thenReturn(chapter);
            when(historyMapper.selectByUserAndNovel(userId, novelId)).thenReturn(null);

            // When
            historyService.addOrUpdateHistory(userId, novelId, chapterId);

            // Then: Verify a new record is inserted.
            verify(historyMapper).insertSelective(any(History.class));
            verify(historyMapper, never()).updateByPrimaryKeySelective(any());
        }


        @Test
        @DisplayName("Should update chapterId and timestamp when a history record for the novel already exists")
        void shouldUpdateExistingRecord_whenHistoryExists() {
            // Given: A history record for this novel but a different chapter already exists.
            History existingHistory = new History();
            existingHistory.setId(1);
            existingHistory.setUserId(userId);
            existingHistory.setNovelId(novelId);
            existingHistory.setChapterId(100); // Old chapter

            Novel novel = new Novel();
            novel.setId(novelId);

            Chapter chapter = new Chapter();
            chapter.setId(chapterId);
            chapter.setNovelId(novelId); // 关键：设置章节属于该小说

            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
            when(chapterMapper.selectByPrimaryKey(chapterId)).thenReturn(chapter);
            when(historyMapper.selectByUserAndNovel(userId, novelId)).thenReturn(existingHistory);

            // When
            historyService.addOrUpdateHistory(userId, novelId, chapterId);

            // Then: Verify the existing record is updated with the new chapterId.
            verify(historyMapper, never()).insertSelective(any());
            verify(historyMapper).updateByPrimaryKeySelective(argThat(history ->
                    history.getId().equals(1) &&
                            history.getChapterId().equals(chapterId) && // Crucially, chapterId is updated
                            history.getUpdateTime() != null
            ));
        }


        @Test
        @DisplayName("Should throw ResourceNotFoundException if the novel does not exist")
        void shouldThrowException_whenNovelNotFound() {
            // Given: Novel does not exist.
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(null);

            // When & Then: Expect an exception.
            assertThrows(ResourceNotFoundException.class, () -> {
                historyService.addOrUpdateHistory(userId, novelId, chapterId);
            });
            verify(historyMapper, never()).insertSelective(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if the chapter does not exist")
        void shouldThrowException_whenChapterNotFound() {
            // Given: Novel exists, but Chapter does not.
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(new Novel());
            when(chapterMapper.selectByPrimaryKey(chapterId)).thenReturn(null);

            // When & Then: Expect an exception.
            assertThrows(ResourceNotFoundException.class, () -> {
                historyService.addOrUpdateHistory(userId, novelId, chapterId);
            });
            verify(historyMapper, never()).insertSelective(any());
        }
    }

    @Nested
    @DisplayName("getUserHistory Tests")
    class GetUserHistory {
        @Test
        @DisplayName("Should return a paginated list of rich DTOs using batch operations")
        void shouldReturnRichDTOs_withBatchOperations() {
            // Given: Mocks for a full, successful data retrieval.
            int page = 0, size = 10;
            History history1 = createHistory(1, userId, 1, 101);
            History history2 = createHistory(2, userId, 2, 201);
            List<History> histories = Arrays.asList(history1, history2);

            Novel novel1 = createNovel(1, 1, "Novel One");
            Novel novel2 = createNovel(2, 2, "Novel Two");
            Map<Integer, Novel> novelMap = Map.of(1, novel1, 2, novel2);

            Chapter chapter1 = createChapter(101, 1);
            Chapter chapter2 = createChapter(201, 2);
            Map<Integer, Chapter> chapterMap = Map.of(101, chapter1, 201, chapter2);

            Category category1 = createCategory(1, "Cat One");
            Category category2 = createCategory(2, "Cat Two");
            Map<Integer, Category> categoryMap = Map.of(1, category1, 2, category2);

            Map<Integer, Boolean> libraryStatusMap = Map.of(1, true, 2, false);

            when(historyMapper.countByUserId(userId)).thenReturn(2L);
            when(historyMapper.selectByUserIdWithPagination(userId, 0, 10)).thenReturn(histories);
            when(novelMapper.selectByIds(anyList())).thenReturn(new ArrayList<>(novelMap.values()));
            when(chapterMapper.selectByIds(anyList())).thenReturn(new ArrayList<>(chapterMap.values()));
            when(categoryMapper.selectByIds(anyList())).thenReturn(new ArrayList<>(categoryMap.values()));
            when(libraryService.checkNovelsInLibrary(eq(userId), anyList())).thenReturn(libraryStatusMap);

            // When
            PageResponseDTO<HistoryResponseDTO> result = historyService.getUserHistory(userId, page, size);

            // Then: Assertions to confirm the rich DTO is correctly assembled.
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());

            HistoryResponseDTO dto1 = result.getContent().get(0);
            assertEquals("Novel One", dto1.getNovelTitle());
            assertEquals("Cat One", dto1.getCategoryName());
            assertEquals(1, dto1.getChapterNumber());
            assertTrue(dto1.isInLibrary());

            HistoryResponseDTO dto2 = result.getContent().get(1);
            assertEquals("Novel Two", dto2.getNovelTitle());
            assertFalse(dto2.isInLibrary());

            // Verify batch methods were called exactly once.
            verify(novelMapper, times(1)).selectByIds(anyList());
            verify(chapterMapper, times(1)).selectByIds(anyList());
            verify(categoryMapper, times(1)).selectByIds(anyList());
            verify(libraryService, times(1)).checkNovelsInLibrary(any(), anyList());
        }

        @Test
        @DisplayName("Should return an empty page when user has no history")
        void shouldReturnEmptyPage_whenNoHistory() {
            // Given: Mocks for an empty history.
            when(historyMapper.countByUserId(userId)).thenReturn(0L);
            when(historyMapper.selectByUserIdWithPagination(userId, 0, 10)).thenReturn(Collections.emptyList());

            // When
            PageResponseDTO<HistoryResponseDTO> result = historyService.getUserHistory(userId, 0, 10);

            // Then: Assertions for an empty page response.
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
            verify(novelMapper, never()).selectByIds(anyList());
        }
    }

    @Nested
    @DisplayName("deleteHistory Tests")
    class DeleteHistory {
        @Test
        @DisplayName("Should successfully delete a history record owned by the user")
        void shouldDeleteHistory_whenOwnedByUser() {
            // Given: A history record that belongs to the user.
            History history = new History();
            history.setId(1);
            history.setUserId(userId);
            when(historyMapper.selectByPrimaryKey(1)).thenReturn(history);

            // When
            historyService.deleteHistory(userId, 1);

            // Then: Verify the delete method was called.
            verify(historyMapper).deleteByPrimaryKey(1);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when trying to delete a non-existent record")
        void shouldThrowException_whenDeletingNonExistentRecord() {
            // Given: The record does not exist.
            when(historyMapper.selectByPrimaryKey(999)).thenReturn(null);

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> {
                historyService.deleteHistory(userId, 999);
            });
            verify(historyMapper, never()).deleteByPrimaryKey(anyInt());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when trying to delete a record owned by another user")
        void shouldThrowException_whenDeletingRecordOfAnotherUser() {
            // Given: The record belongs to a different user.
            History history = new History();
            history.setId(1);
            history.setUserId(UUID.randomUUID()); // Different user
            when(historyMapper.selectByPrimaryKey(1)).thenReturn(history);

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> {
                historyService.deleteHistory(userId, 1);
            });
            verify(historyMapper, never()).deleteByPrimaryKey(anyInt());
        }
    }

    @Nested
    @DisplayName("clearHistory Tests")
    class ClearHistory {
        @Test
        @DisplayName("Should call the mapper to delete all history for the user")
        void shouldDeleteAllHistoryForUser() {
            // When
            historyService.clearHistory(userId);

            // Then
            verify(historyMapper).deleteByUserId(userId);
        }
    }


    // Helper methods for creating mock entities
    private History createHistory(Integer id, UUID userId, Integer novelId, Integer chapterId) {
        return new History(id, UUID.randomUUID(), userId, novelId, chapterId, new Date(), new Date());
    }
    private Novel createNovel(Integer id, Integer categoryId, String title) {
        Novel n = new Novel();
        n.setId(id);
        n.setCategoryId(categoryId);
        n.setTitle(title);
        return n;
    }
    private Chapter createChapter(Integer id, Integer number) {
        Chapter c = new Chapter();
        c.setId(id);
        c.setChapterNumber(number);
        return c;
    }
    private Category createCategory(Integer id, String name) {
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        return c;
    }
}