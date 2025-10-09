package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Category;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NovelService with current method signatures and UUID authorId.
 */
public class NovelServiceTest {

    private NovelMapper novelMapper;
    private CategoryService categoryService;
    private NovelService novelService;

    @BeforeEach
    void setUp() {
        novelMapper = Mockito.mock(NovelMapper.class);
        categoryService = Mockito.mock(CategoryService.class);

        novelService = new NovelService();
        try {
            java.lang.reflect.Field f1 = NovelService.class.getDeclaredField("novelMapper");
            f1.setAccessible(true);
            f1.set(novelService, novelMapper);

            java.lang.reflect.Field f2 = NovelService.class.getDeclaredField("categoryService");
            f2.setAccessible(true);
            f2.set(novelService, categoryService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createNovel_ShouldInsertWithDefaults_AndReturnDto() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String authorName = "Author";
        NovelCreateRequestDTO req = new NovelCreateRequestDTO();
        req.setTitle("My Title");
        req.setCategoryId(10);
        req.setCoverImgBase64("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD");
        req.setSynopsis("synopsis");
        req.setIsCompleted(true);

        when(categoryService.getCategoryById(10)).thenReturn(new Category());
        when(novelMapper.insertSelective(any(Novel.class))).thenReturn(1);

        // Act
        NovelDetailResponseDTO response = novelService.createNovel(userId, authorName, req);

        // Assert interactions
        verify(novelMapper, times(1)).insertSelective(argThat(n -> {
            assertNotNull(n.getUuid());
            assertEquals(userId, n.getAuthorId());
            assertEquals(authorName, n.getAuthorName());
            assertEquals("My Title", n.getTitle());
            assertEquals(Integer.valueOf(10), n.getCategoryId());
            assertTrue(Boolean.TRUE.equals(n.getIsValid()));
            assertEquals(Integer.valueOf(0), n.getChapterCnt());
            assertEquals(Long.valueOf(0L), n.getWordCnt());
            assertEquals(Float.valueOf(0.0f), n.getAvgRating());
            assertEquals(Integer.valueOf(0), n.getReviewCnt());
            assertEquals(Long.valueOf(0L), n.getViewCnt());
            assertEquals(Integer.valueOf(0), n.getVoteCnt());
            assertEquals(Float.valueOf(0.0f), n.getYuanCnt());
            assertNotNull(n.getCreateTime());
            assertNotNull(n.getUpdateTime());
            return true;
        }));

        assertNotNull(response);
        assertEquals(Integer.valueOf(10), response.getCategoryId());
    }

    @Test
    void createNovel_CategoryNotFound_ShouldThrow() {
        UUID userId = UUID.randomUUID();
        NovelCreateRequestDTO req = new NovelCreateRequestDTO();
        req.setTitle("My Title");
        req.setCategoryId(999);

        when(categoryService.getCategoryById(999)).thenThrow(new RuntimeException("Category not found"));

        assertThrows(IllegalArgumentException.class, () -> novelService.createNovel(userId, "A", req));
        verify(novelMapper, never()).insertSelective(any());
    }

    @Test
    void updateNovel_ShouldUpdateSelective() {
        Integer novelId = 1;
        Novel existing = new Novel();
        existing.setId(novelId);
        existing.setAuthorId(UUID.randomUUID());
        existing.setIsValid(true);
        existing.setStatus(0); // DRAFT
        existing.setCreateTime(new Date());
        existing.setUpdateTime(new Date());

        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(existing);
        when(novelMapper.updateByPrimaryKeySelective(any(Novel.class))).thenReturn(1);
        when(categoryService.getCategoryById(20)).thenReturn(new Category());

        NovelUpdateRequestDTO req = new NovelUpdateRequestDTO();
        req.setTitle("New Title");
        req.setCategoryId(20);

        NovelDetailResponseDTO response = novelService.updateNovel(novelId, req);

        verify(novelMapper, times(1)).updateByPrimaryKeySelective(argThat(n -> {
            assertEquals(novelId, n.getId());
            assertEquals("New Title", n.getTitle());
            assertEquals(Integer.valueOf(20), n.getCategoryId());
            return true;
        }));

        assertNotNull(response);
    }

    @Test
    void updateNovel_CategoryNotFound_ShouldThrow() {
        Integer novelId = 2;
        Novel existing = new Novel();
        existing.setId(novelId);
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(existing);

        NovelUpdateRequestDTO req = new NovelUpdateRequestDTO();
        req.setCategoryId(999);
        when(categoryService.getCategoryById(999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> novelService.updateNovel(novelId, req));
        verify(novelMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void getNovel_Valid_ShouldReturnResponse() {
        Integer novelId = 4;
        Novel ok = new Novel();
        ok.setId(novelId);
        ok.setIsValid(true);
        ok.setStatus(1); // PUBLISHED
        ok.setTitle("OK");
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(ok);

        NovelDetailResponseDTO response = novelService.getNovel(novelId);
        assertNotNull(response);
        assertEquals("OK", response.getTitle());
    }

    @Test
    void listNovelsWithPagination_ShouldReturnPaginatedResults() {
        // Arrange
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(0, 10, "createTime", "desc", null, null, null, null, null);
        
        Novel novel1 = createTestNovel(1, "Novel 1", UUID.randomUUID(), "Author 1", 1);
        Novel novel2 = createTestNovel(2, "Novel 2", UUID.randomUUID(), "Author 2", 1);
        List<Novel> novels = Arrays.asList(novel1, novel2);
        
        when(novelMapper.selectNovelsWithPagination(request)).thenReturn(novels);
        when(novelMapper.countNovels(request)).thenReturn(25L);
        
        // Act
        PageResponseDTO<NovelDetailResponseDTO> response = novelService.listNovelsWithPagination(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(25L, response.getTotalElements());
        assertEquals(3, response.getTotalPages()); // 25/10 = 3 pages
        assertEquals(0, response.getCurrentPage());
        assertEquals(10, response.getSize());
        assertTrue(response.isFirst());
        assertFalse(response.isLast());
        assertTrue(response.isHasNext());
        assertFalse(response.isHasPrevious());
    }

    @Test
    void listNovelsWithPagination_ShouldHandleEmptyResults() {
        // Arrange
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(0, 10, "createTime", "desc", null, null, null, null, null);
        
        when(novelMapper.selectNovelsWithPagination(request)).thenReturn(Arrays.asList());
        when(novelMapper.countNovels(request)).thenReturn(0L);
        
        // Act
        PageResponseDTO<NovelDetailResponseDTO> response = novelService.listNovelsWithPagination(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        assertEquals(0L, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getCurrentPage());
        assertEquals(10, response.getSize());
        assertTrue(response.isFirst());
        assertTrue(response.isLast());
        assertFalse(response.isHasNext());
        assertFalse(response.isHasPrevious());
    }

    @Test
    void listNovelsWithPagination_ShouldValidateAndSetDefaults() {
        // Arrange - Test with null values
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(null, null, null, null, null, null, null, null, null);
        
        when(novelMapper.selectNovelsWithPagination(any())).thenReturn(Arrays.asList());
        when(novelMapper.countNovels(any())).thenReturn(0L);
        
        // Act
        novelService.listNovelsWithPagination(request);
        
        // Assert - Verify defaults were set
        verify(novelMapper).selectNovelsWithPagination(argThat(req -> 
            req.getPage() == 0 && 
            req.getSize() == 10 && 
            "createTime".equals(req.getSort()) && 
            "desc".equals(req.getOrder())
        ));
    }

    @Test
    void listNovelsWithPagination_ShouldLimitMaxSize() {
        // Arrange
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(0, 200, "createTime", "desc", null, null, null, null, null);
        
        when(novelMapper.selectNovelsWithPagination(any())).thenReturn(Arrays.asList());
        when(novelMapper.countNovels(any())).thenReturn(0L);
        
        // Act
        novelService.listNovelsWithPagination(request);
        
        // Assert - Verify size was limited to 100
        verify(novelMapper).selectNovelsWithPagination(argThat(req -> req.getSize() == 100));
    }

    @Test
    void listNovelsWithPagination_ShouldHandleNegativePage() {
        // Arrange
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(-1, 10, "createTime", "desc", null, null, null, null, null);
        
        when(novelMapper.selectNovelsWithPagination(any())).thenReturn(Arrays.asList());
        when(novelMapper.countNovels(any())).thenReturn(0L);
        
        // Act
        novelService.listNovelsWithPagination(request);
        
        // Assert - Verify page was set to 0
        verify(novelMapper).selectNovelsWithPagination(argThat(req -> req.getPage() == 0));
    }

    @Test
    void submitForReview_ShouldChangeStatusToUnderReview_WhenValidRequest() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        Novel novel = createTestNovel(novelId, "Test Novel", userId, "Test Author", 1);
        novel.setStatus(0); // DRAFT
        
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
        when(novelMapper.updateByPrimaryKeySelective(any())).thenReturn(1);
        
        // Act
        NovelDetailResponseDTO result = novelService.submitForReview(novelId, userId);
        
        // Assert
        assertNotNull(result);
        verify(novelMapper).updateByPrimaryKeySelective(argThat(n -> n.getStatus() == 1)); // UNDER_REVIEW
    }

    @Test
    void approveNovel_ShouldChangeStatusToPublished_WhenUnderReview() {
        // Arrange
        Integer novelId = 1;
        Novel novel = createTestNovel(novelId, "Test Novel", UUID.randomUUID(), "Test Author", 1);
        novel.setStatus(1); // UNDER_REVIEW
        
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
        when(novelMapper.updateByPrimaryKeySelective(any())).thenReturn(1);
        
        // Act
        NovelDetailResponseDTO result = novelService.approveNovel(novelId);
        
        // Assert
        assertNotNull(result);
        verify(novelMapper).updateByPrimaryKeySelective(argThat(n -> 
            n.getStatus() == 2 && n.getPublishTime() != null)); // PUBLISHED with publish time
    }

    @Test
    void getNovelsUnderReview_ShouldReturnPaginatedResults() {
        // Arrange
        Novel novel1 = createTestNovel(1, "Novel 1", UUID.randomUUID(), "Author 1", 1);
        Novel novel2 = createTestNovel(2, "Novel 2", UUID.randomUUID(), "Author 2", 1);
        novel1.setStatus(1); // UNDER_REVIEW
        novel2.setStatus(1); // UNDER_REVIEW
        
        when(novelMapper.selectNovelsWithPagination(any())).thenReturn(Arrays.asList(novel1, novel2));
        when(novelMapper.countNovels(any())).thenReturn(2L);
        
        // Act
        PageResponseDTO<NovelDetailResponseDTO> result = novelService.getNovelsUnderReview(0, 10);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(novelMapper).selectNovelsWithPagination(argThat(req -> 
            "UNDER_REVIEW".equals(req.getStatus())
        ));
    }

    private Novel createTestNovel(Integer id, String title, UUID authorId, String authorName, Integer categoryId) {
        Novel novel = new Novel();
        novel.setId(id);
        novel.setUuid(UUID.randomUUID());
        novel.setTitle(title);
        novel.setAuthorId(authorId);
        novel.setAuthorName(authorName);
        novel.setCategoryId(categoryId);
        novel.setSynopsis("Test synopsis");
        novel.setCoverImgUrl("test-cover.jpg");
        novel.setStatus(1); // PUBLISHED
        novel.setIsCompleted(false);
        novel.setIsValid(true);
        novel.setChapterCnt(5);
        novel.setWordCnt(10000L);
        novel.setAvgRating(4.5f);
        novel.setReviewCnt(10);
        novel.setViewCnt(1000L);
        novel.setVoteCnt(50);
        novel.setYuanCnt(0.0f);
        novel.setCreateTime(new Date());
        novel.setUpdateTime(new Date());
        novel.setPublishTime(new Date());
        return novel;
    }

    @Test
    void archiveNovel_ShouldSetStatusToArchivedAndIsValidFalse() {
        // Arrange
        Integer novelId = 123;
        Novel existingNovel = new Novel();
        existingNovel.setId(novelId);
        existingNovel.setTitle("Test Novel");
        existingNovel.setIsValid(true);
        existingNovel.setStatus(0); // DRAFT

        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(existingNovel);
        when(novelMapper.updateByPrimaryKeySelective(any(Novel.class))).thenReturn(1);

        // Act
        NovelDetailResponseDTO result = novelService.archiveNovel(novelId);

        // Assert
        verify(novelMapper).updateByPrimaryKeySelective(argThat(novel -> {
            return novel.getStatus() == 4 && // ARCHIVED
                   !novel.getIsValid() &&
                   novel.getUpdateTime() != null;
        }));
        assertNotNull(result);
    }

    @Test
    void archiveNovel_NovelNotFound_ThrowsException() {
        // Arrange
        Integer novelId = 123;
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            novelService.archiveNovel(novelId);
        });
    }
}


