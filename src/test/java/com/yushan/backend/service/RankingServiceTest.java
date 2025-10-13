package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.dto.NovelDetailResponseDTO;
import com.yushan.backend.dto.NovelRankDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.Category;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private NovelMapper novelMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CategoryService categoryService;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private RankingService rankingService;

    @Nested
    @DisplayName("rankNovel Tests")
    class RankNovel {

        @Test
        @DisplayName("Should return paginated novels from Redis with category names")
        void shouldReturnPagedNovelsFromRedis() {
            // Given
            int page = 0, size = 10;
            String redisKey = "ranking:novel:view:all";
            Set<String> novelIdsFromRedis = Set.of("1", "2");

            Novel novel1 = createMockNovel(1, 10);
            Novel novel2 = createMockNovel(2, 20);
            List<Novel> novelsFromDb = Arrays.asList(novel1, novel2);
            Map<Integer, String> categoryMap = Map.of(10, "Fantasy", 20, "Sci-Fi");

            when(redisUtil.zReverseRange(redisKey, 0, 9)).thenReturn(novelIdsFromRedis);
            when(redisUtil.zCard(redisKey)).thenReturn(100L);
            when(novelMapper.selectByIds(anyList())).thenReturn(novelsFromDb);
            when(categoryService.getCategoryMapByIds(anyList())).thenReturn(categoryMap);

            // When
            PageResponseDTO<NovelDetailResponseDTO> result = rankingService.rankNovel(page, size, "view", null, "overall");

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(100, result.getTotalElements());
            assertEquals("Fantasy", result.getContent().get(0).getCategoryName());
            assertEquals("Sci-Fi", result.getContent().get(1).getCategoryName());

            verify(redisUtil).zReverseRange(eq(redisKey), eq(0L), eq(9L));
            verify(novelMapper).selectByIds(anyList());
            verify(categoryService).getCategoryMapByIds(anyList());
        }

        @Test
        @DisplayName("Should return empty page if Redis returns no data")
        void shouldReturnEmptyWhenRedisIsEmpty() {
            // Given
            String redisKey = "ranking:novel:vote:1";

            // When
            PageResponseDTO<NovelDetailResponseDTO> result = rankingService.rankNovel(0, 10, "vote", 1, "overall");

            // Then
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
            verify(novelMapper, never()).selectByIds(anyList());
        }
    }

    @Nested
    @DisplayName("getBestNovelRank Tests")
    class GetBestNovelRank {
        @Test
        @DisplayName("Should return the best rank among all leaderboards")
        void shouldReturnBestRank() {
            // Given
            Novel novel = createMockNovel(42, 10);
            when(novelMapper.selectByPrimaryKey(42)).thenReturn(novel);
            when(categoryService.getCategoryById(10)).thenReturn(new Category(10, "Fantasy", null, null, true, null, null));

            // Mock ranks: best rank is 5th on Category Votes
            when(redisUtil.zReverseRank("ranking:novel:view:all", "42")).thenReturn(10L); // Rank 11
            when(redisUtil.zScore("ranking:novel:view:all", "42")).thenReturn(5000.0);
            when(redisUtil.zReverseRank("ranking:novel:vote:all", "42")).thenReturn(8L);  // Rank 9
            when(redisUtil.zScore("ranking:novel:vote:all", "42")).thenReturn(500.0);
            when(redisUtil.zReverseRank("ranking:novel:view:10", "42")).thenReturn(null); // Not in this ranking
            when(redisUtil.zReverseRank("ranking:novel:vote:10", "42")).thenReturn(4L);  // Rank 5 (The Best)
            when(redisUtil.zScore("ranking:novel:vote:10", "42")).thenReturn(100.0);

            // When
            NovelRankDTO result = rankingService.getBestNovelRank(42);

            // Then
            assertNotNull(result);
            assertEquals(5, result.getRank());
            assertEquals("Fantasy Votes Ranking", result.getRankType());
            assertEquals(100.0, result.getScore());
        }

        @Test
        @DisplayName("Should return null if novel is not in any top 100 ranking")
        void shouldReturnNullWhenNotInAnyRanking() {
            // Given
            Novel novel = createMockNovel(42, 10);
            when(novelMapper.selectByPrimaryKey(42)).thenReturn(novel);
            when(categoryService.getCategoryById(10)).thenReturn(new Category());
            when(redisUtil.zReverseRank(anyString(), anyString())).thenReturn(null);

            // When
            NovelRankDTO result = rankingService.getBestNovelRank(42);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should throw exception if novel does not exist")
        void shouldThrowExceptionWhenNovelNotFound() {
            assertThrows(ResourceNotFoundException.class, () -> {
                rankingService.getBestNovelRank(999);
            });
        }

    }

    @Nested
    @DisplayName("rankUser and rankAuthor Tests")
    class RankUserAndAuthor {

        @Test
        @DisplayName("rankUser should return paginated user profiles from Redis")
        void rankUser_ShouldSucceed() {
            // Given
            String redisKey = "ranking:user:exp";
            Set<String> userUuids = Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

            when(redisUtil.zReverseRange(redisKey, 0, 9)).thenReturn(userUuids);
            when(redisUtil.zCard(redisKey)).thenReturn(2L);
            when(userMapper.selectByUuids(anyList())).thenReturn(userUuids.stream().map(this::createMockUser).collect(Collectors.toList()));

            // When
            PageResponseDTO<UserProfileResponseDTO> result = rankingService.rankUser(0, 10, "overall");

            // Then
            assertEquals(2, result.getContent().size());
            verify(redisUtil).zReverseRange(eq(redisKey), eq(0L), eq(9L));
            verify(userMapper).selectByUuids(anyList());
        }

        @Test
        @DisplayName("rankAuthor should return paginated author DTOs from Redis")
        void rankAuthor_ShouldSucceed() {
            // Given
            String redisKey = "ranking:author:vote";
            Set<String> authorUuids = Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

            when(redisUtil.zReverseRange(redisKey, 0, 9)).thenReturn(authorUuids);
            when(redisUtil.zCard(redisKey)).thenReturn(2L);
            when(novelMapper.selectAuthorsByUuids(anyList())).thenReturn(
                    authorUuids.stream()
                            .map(this::createMockAuthor)
                            .collect(Collectors.toList())
            );

            // When
            PageResponseDTO<AuthorResponseDTO> result = rankingService.rankAuthor(0, 10, "vote", "overall");

            // Then
            assertEquals(2, result.getContent().size());
            verify(redisUtil).zReverseRange(eq(redisKey), eq(0L), eq(9L));
            verify(novelMapper).selectAuthorsByUuids(anyList());
        }
        private User createMockUser(String uuid) {
            User user = new User();
            user.setUuid(UUID.fromString(uuid));
            user.setUsername("testuser");
            return user;
        }

        private AuthorResponseDTO createMockAuthor(String uuid) {
            AuthorResponseDTO author = new AuthorResponseDTO();
            author.setUuid(uuid);
            author.setUsername("testauthor");
            return author;
        }
    }

    // Helper methods for creating mock data
    private Novel createMockNovel(Integer id, Integer categoryId) {
        Novel novel = new Novel();
        novel.setId(id);
        novel.setTitle("Test Novel " + id);
        novel.setCategoryId(categoryId);
        novel.setStatus(2);
        return novel;
    }

    private User createMockUser(String uuid) {
        User user = new User();
        user.setUuid(UUID.fromString(uuid));
        user.setUsername("testuser");
        return user;
    }

    private AuthorResponseDTO createMockAuthor(String uuid) {
        AuthorResponseDTO author = new AuthorResponseDTO();
        author.setUuid(uuid);
        author.setUsername("testauthor");
        return author;
    }

}