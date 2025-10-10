package com.yushan.backend.service;

import com.yushan.backend.dao.HistoryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.dto.HistoryResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.entity.History;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private UUID testUserId;
    private Integer testNovelId;
    private Integer testChapterId;
    private Integer testHistoryId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testNovelId = 1;
        testChapterId = 5;
        testHistoryId = 1;
    }

    @Test
    void addOrUpdateHistory_ShouldCreateNewHistory_WhenHistoryNotExists() {
        // Given
        Novel mockNovel = new Novel();
        mockNovel.setId(testNovelId);
        Chapter mockChapter = new Chapter();
        mockChapter.setId(testChapterId);
        mockChapter.setNovelId(testNovelId);
        
        when(novelMapper.selectByPrimaryKey(testNovelId)).thenReturn(mockNovel);
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(mockChapter);
        when(historyMapper.selectByUserAndNovel(testUserId, testNovelId))
                .thenReturn(null);
        when(historyMapper.insertSelective(any(History.class))).thenReturn(1);

        // When
        historyService.addOrUpdateHistory(testUserId, testNovelId, testChapterId);

        // Then
        verify(historyMapper).selectByUserAndNovel(testUserId, testNovelId);
        verify(historyMapper).insertSelective(any(History.class));
        verify(historyMapper, never()).updateByPrimaryKey(any(History.class));
    }

    @Test
    void addOrUpdateHistory_ShouldUpdateExistingHistory_WhenHistoryExists() {
        // Given
        Novel mockNovel = new Novel();
        mockNovel.setId(testNovelId);
        Chapter mockChapter = new Chapter();
        mockChapter.setId(testChapterId);
        mockChapter.setNovelId(testNovelId);
        
        History existingHistory = new History();
        existingHistory.setId(testHistoryId);
        existingHistory.setUserId(testUserId);
        existingHistory.setNovelId(testNovelId);
        existingHistory.setChapterId(testChapterId);
        existingHistory.setUpdateTime(new Date());

        when(novelMapper.selectByPrimaryKey(testNovelId)).thenReturn(mockNovel);
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(mockChapter);
        when(historyMapper.selectByUserAndNovel(testUserId, testNovelId))
                .thenReturn(existingHistory);
        when(historyMapper.updateByPrimaryKeySelective(any(History.class))).thenReturn(1);

        // When
        historyService.addOrUpdateHistory(testUserId, testNovelId, testChapterId);

        // Then
        verify(historyMapper).selectByUserAndNovel(testUserId, testNovelId);
        verify(historyMapper).updateByPrimaryKeySelective(any(History.class));
        verify(historyMapper, never()).insert(any(History.class));
    }

    @Test
    void getUserHistory_ShouldReturnHistoryPage_WhenValidRequest() {
        // Given
        int page = 0;
        int size = 20;
        List<History> mockHistories = createMockHistories();

        when(historyMapper.selectByUserIdWithPagination(testUserId, page * size, size))
                .thenReturn(mockHistories);
        when(historyMapper.countByUserId(testUserId)).thenReturn(10L);

        // When
        PageResponseDTO<HistoryResponseDTO> result = historyService.getUserHistory(testUserId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        verify(historyMapper).selectByUserIdWithPagination(testUserId, page * size, size);
        verify(historyMapper).countByUserId(testUserId);
    }

    @Test
    void deleteHistory_ShouldDeleteHistory_WhenValidId() {
        // Given
        History mockHistory = new History();
        mockHistory.setId(testHistoryId);
        mockHistory.setUserId(testUserId);
        
        when(historyMapper.selectByPrimaryKey(testHistoryId)).thenReturn(mockHistory);
        when(historyMapper.deleteByPrimaryKey(testHistoryId)).thenReturn(1);

        // When
        historyService.deleteHistory(testUserId, testHistoryId);

        // Then
        verify(historyMapper).deleteByPrimaryKey(testHistoryId);
    }

    @Test
    void clearHistory_ShouldDeleteAllUserHistory_WhenCalled() {
        // Given
        when(historyMapper.deleteByUserId(testUserId)).thenReturn(5);

        // When
        historyService.clearHistory(testUserId);

        // Then
        verify(historyMapper).deleteByUserId(testUserId);
    }

    @Test
    void getUserHistory_ShouldReturnEmptyPage_WhenNoHistory() {
        // Given
        int page = 0;
        int size = 20;

        when(historyMapper.selectByUserIdWithPagination(testUserId, page * size, size))
                .thenReturn(new ArrayList<>());
        when(historyMapper.countByUserId(testUserId)).thenReturn(0L);

        // When
        PageResponseDTO<HistoryResponseDTO> result = historyService.getUserHistory(testUserId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void addOrUpdateHistory_ShouldHandleDatabaseError_WhenInsertFails() {
        // Given
        Novel mockNovel = new Novel();
        mockNovel.setId(testNovelId);
        Chapter mockChapter = new Chapter();
        mockChapter.setId(testChapterId);
        mockChapter.setNovelId(testNovelId);
        
        when(novelMapper.selectByPrimaryKey(testNovelId)).thenReturn(mockNovel);
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(mockChapter);
        when(historyMapper.selectByUserAndNovel(testUserId, testNovelId))
                .thenReturn(null);
        when(historyMapper.insertSelective(any(History.class))).thenReturn(0);

        // When & Then
        assertDoesNotThrow(() -> historyService.addOrUpdateHistory(testUserId, testNovelId, testChapterId));
        verify(historyMapper).insertSelective(any(History.class));
    }

    private List<History> createMockHistories() {
        List<History> histories = new ArrayList<>();
        
        History history1 = new History();
        history1.setId(1);
        history1.setUserId(testUserId);
        history1.setNovelId(testNovelId);
        history1.setChapterId(testChapterId);
        history1.setUpdateTime(new Date());
        histories.add(history1);

        History history2 = new History();
        history2.setId(2);
        history2.setUserId(testUserId);
        history2.setNovelId(2);
        history2.setChapterId(6);
        history2.setUpdateTime(new Date());
        histories.add(history2);

        return histories;
    }
}