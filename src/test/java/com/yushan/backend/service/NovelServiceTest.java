package com.yushan.backend.service;

import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.NovelCreateRequestDTO;
import com.yushan.backend.dto.NovelResponseDTO;
import com.yushan.backend.dto.NovelUpdateRequestDTO;
import com.yushan.backend.entity.Category;
import com.yushan.backend.entity.Novel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NovelService with current method signatures and UUID authorId.
 */
public class NovelServiceTest {

    private NovelMapper novelMapper;
    private CategoryMapper categoryMapper;
    private NovelService novelService;

    @BeforeEach
    void setUp() {
        novelMapper = Mockito.mock(NovelMapper.class);
        categoryMapper = Mockito.mock(CategoryMapper.class);

        novelService = new NovelService();
        try {
            java.lang.reflect.Field f1 = NovelService.class.getDeclaredField("novelMapper");
            f1.setAccessible(true);
            f1.set(novelService, novelMapper);

            java.lang.reflect.Field f2 = NovelService.class.getDeclaredField("categoryMapper");
            f2.setAccessible(true);
            f2.set(novelService, categoryMapper);
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
        req.setCoverImgUrl("http://img");
        req.setSynopsis("synopsis");
        req.setIsCompleted(true);

        when(categoryMapper.selectByPrimaryKey(10)).thenReturn(new Category());
        when(novelMapper.insertSelective(any(Novel.class))).thenReturn(1);

        // Act
        NovelResponseDTO response = novelService.createNovel(userId, authorName, req);

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

        when(categoryMapper.selectByPrimaryKey(999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> novelService.createNovel(userId, "A", req));
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
        when(categoryMapper.selectByPrimaryKey(20)).thenReturn(new Category());

        NovelUpdateRequestDTO req = new NovelUpdateRequestDTO();
        req.setTitle("New Title");
        req.setCategoryId(20);

        NovelResponseDTO response = novelService.updateNovel(novelId, req);

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
        when(categoryMapper.selectByPrimaryKey(999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> novelService.updateNovel(novelId, req));
        verify(novelMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void getNovel_ArchivedOrInvalid_ShouldThrowNotFound() {
        Integer novelId = 3;

        Novel archived = new Novel();
        archived.setId(novelId);
        archived.setIsValid(true);
        archived.setStatus(2); // ARCHIVED

        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(archived);
        assertThrows(RuntimeException.class, () -> novelService.getNovel(novelId));

        Novel invalid = new Novel();
        invalid.setId(novelId);
        invalid.setIsValid(false);
        invalid.setStatus(1);

        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(invalid);
        assertThrows(RuntimeException.class, () -> novelService.getNovel(novelId));
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

        NovelResponseDTO response = novelService.getNovel(novelId);
        assertNotNull(response);
        assertEquals("OK", response.getTitle());
    }
}


