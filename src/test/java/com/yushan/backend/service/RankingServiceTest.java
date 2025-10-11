package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.dto.NovelDetailResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private NovelMapper novelMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CategoryService categoryService; // [NEW] Mock the CategoryService

    @InjectMocks
    private RankingService rankingService;

    @Nested
    @DisplayName("rankNovel Tests")
    class RankNovel {

        @Test
        @DisplayName("Should return paged response with category names")
        void shouldReturnPagedNovelsWithCategoryNames() {
            // Given
            Novel novel1 = createMockNovel(1, 10); // novelId=1, categoryId=10
            Novel novel2 = createMockNovel(2, 20); // novelId=2, categoryId=20
            List<Novel> mockNovels = Arrays.asList(novel1, novel2);

            Map<Integer, String> categoryMap = Map.of(10, "Fantasy", 20, "Sci-Fi");

            when(novelMapper.selectNovelsByRanking(any(), anyString(), anyInt(), anyInt())).thenReturn(mockNovels);
            when(novelMapper.countNovelsByRanking(any())).thenReturn(2L);
            // [NEW] Mock the batch call to categoryService
            when(categoryService.getCategoryMapByIds(anyList())).thenReturn(categoryMap);

            // When
            PageResponseDTO<NovelDetailResponseDTO> result = rankingService.rankNovel(0, 10, "view", 1, "overall");

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(2L, result.getTotalElements());

            NovelDetailResponseDTO dto1 = result.getContent().get(0);
            assertEquals(1, dto1.getId());
            assertEquals(10, dto1.getCategoryId());
            assertEquals("Fantasy", dto1.getCategoryName()); // Verify category name

            NovelDetailResponseDTO dto2 = result.getContent().get(1);
            assertEquals(2, dto2.getId());
            assertEquals(20, dto2.getCategoryId());
            assertEquals("Sci-Fi", dto2.getCategoryName()); // Verify category name

            verify(novelMapper).selectNovelsByRanking(1, "view", 0, 10);
            verify(novelMapper).countNovelsByRanking(1);
            verify(categoryService).getCategoryMapByIds(anyList()); // Verify batch method was called
        }

        @Test
        @DisplayName("Should return empty page when offset exceeds max records")
        void shouldReturnEmptyWhenOffsetExceedsMax() {
            // When
            PageResponseDTO<NovelDetailResponseDTO> result = rankingService.rankNovel(10, 20, "view", null, "overall");

            // Then
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0L, result.getTotalElements());
            verify(categoryService, never()).getCategoryMapByIds(anyList());
        }
    }

    @Nested
    @DisplayName("rankUser and rankAuthor Tests")
    class RankUserAndAuthor {

        @Test
        @DisplayName("rankUser should return paged user profiles")
        void rankUser_ShouldReturnPagedResponse() {
            // Given
            List<User> mockUsers = Arrays.asList(createMockUser(), createMockUser());
            when(userMapper.selectUsersByRanking(anyInt(), anyInt())).thenReturn(mockUsers);
            when(userMapper.countAllUsers()).thenReturn(2L);

            // When
            PageResponseDTO<UserProfileResponseDTO> result = rankingService.rankUser(0, 10, "overall");

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(2L, result.getTotalElements());
            verify(userMapper).selectUsersByRanking(0, 10);
        }

        @Test
        @DisplayName("rankAuthor should return paged author DTOs")
        void rankAuthor_ShouldReturnPagedResponse() {
            // Given
            List<AuthorResponseDTO> mockAuthors = Arrays.asList(new AuthorResponseDTO(), new AuthorResponseDTO());
            when(novelMapper.selectAuthorsByRanking(anyString(), anyInt(), anyInt())).thenReturn(mockAuthors);
            when(userMapper.countAllAuthors()).thenReturn(2L);

            // When
            PageResponseDTO<AuthorResponseDTO> result = rankingService.rankAuthor(0, 10, "vote", "overall");

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(2L, result.getTotalElements());
            verify(novelMapper).selectAuthorsByRanking("vote", 0, 10);
        }
    }

    // Helper methods for creating mock data
    private Novel createMockNovel(Integer id, Integer categoryId) {
        Novel novel = new Novel();
        novel.setId(id);
        novel.setTitle("Test Novel " + id);
        novel.setViewCnt(100L);
        novel.setVoteCnt(50);
        novel.setCoverImgUrl("cover.jpg");
        novel.setCategoryId(categoryId);
        return novel;
    }

    private User createMockUser() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("testuser");
        user.setExp(100f);
        user.setLevel(1);
        user.setAvatarUrl("avatar.jpg");
        return user;
    }
}