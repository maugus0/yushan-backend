package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import com.yushan.backend.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingUpdateServiceTest {

    @Mock
    private NovelMapper novelMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private RankingUpdateService rankingUpdateService;

    @Test
    @DisplayName("updateNovelRankings should fetch all novels and populate Redis ZSETs")
    void testUpdateNovelRankings() {
        // Given
        Novel novel1 = createMockNovel(1, 10, 1000L, 100); // id, categoryId, viewCnt, voteCnt
        Novel novel2 = createMockNovel(2, 20, 2000L, 200);
        List<Novel> allNovels = Arrays.asList(novel1, novel2);
        Set<String> oldKeys = Set.of("ranking:novel:view:all", "ranking:novel:vote:10");

        when(novelMapper.selectAllNovelsForRanking()).thenReturn(allNovels);
        when(redisUtil.keys("ranking:novel:*")).thenReturn(oldKeys);

        // When
        rankingUpdateService.updateNovelRankings();

        // Then
        // 1. Verify old keys are deleted
        verify(redisUtil).delete(oldKeys);

        // 2. Verify total view ranking
        verify(redisUtil).zAdd("ranking:novel:view:all", "1", 1000L);
        verify(redisUtil).zAdd("ranking:novel:view:all", "2", 2000L);

        // 3. Verify total vote ranking
        verify(redisUtil).zAdd("ranking:novel:vote:all", "1", 100);
        verify(redisUtil).zAdd("ranking:novel:vote:all", "2", 200);

        // 4. Verify category-specific view ranking
        verify(redisUtil).zAdd("ranking:novel:view:10", "1", 1000L);
        verify(redisUtil).zAdd("ranking:novel:view:20", "2", 2000L);

        // 5. Verify category-specific vote ranking
        verify(redisUtil).zAdd("ranking:novel:vote:10", "1", 100);
        verify(redisUtil).zAdd("ranking:novel:vote:20", "2", 200);
    }

    @Test
    @DisplayName("updateUserRankings should fetch all users and populate Redis ZSET")
    void testUpdateUserRankings() {
        // Given
        User user1 = createMockUser(UUID.randomUUID(), 150.0f);
        User user2 = createMockUser(UUID.randomUUID(), 250.0f);
        List<User> allUsers = Arrays.asList(user1, user2);

        when(userMapper.selectAllUsersForRanking()).thenReturn(allUsers);

        // When
        rankingUpdateService.updateUserRankings();

        // Then
        // 1. Verify the key is deleted
        verify(redisUtil).delete("ranking:user:exp");

        // 2. Verify users are added to the ranking
        verify(redisUtil).zAdd("ranking:user:exp", user1.getUuid().toString(), 150.0);
        verify(redisUtil).zAdd("ranking:user:exp", user2.getUuid().toString(), 250.0);
    }

    @Test
    @DisplayName("updateAuthorRankings should fetch all authors and populate Redis ZSETs")
    void testUpdateAuthorRankings() {
        // Given
        AuthorResponseDTO author1 = createMockAuthor(UUID.randomUUID().toString(), 10, 100L, 1000L);
        AuthorResponseDTO author2 = createMockAuthor(UUID.randomUUID().toString(), 20, 200L, 2000L);
        List<AuthorResponseDTO> allAuthors = Arrays.asList(author1, author2);

        when(novelMapper.selectAuthorsByRanking(anyString(), eq(0), eq(Integer.MAX_VALUE))).thenReturn(allAuthors);

        // When
        rankingUpdateService.updateAuthorRankings();

        // Then
        // 1. Verify old keys are deleted
        List<String> keysToDelete = List.of("ranking:author:vote", "ranking:author:view", "ranking:author:novelNum");
        verify(redisUtil).delete(keysToDelete);

        // 2. Verify vote count ranking
        verify(redisUtil).zAdd("ranking:author:vote", author1.getUuid(), 100.0);
        verify(redisUtil).zAdd("ranking:author:vote", author2.getUuid(), 200.0);

        // 3. Verify view count ranking
        verify(redisUtil).zAdd("ranking:author:view", author1.getUuid(), 1000.0);
        verify(redisUtil).zAdd("ranking:author:view", author2.getUuid(), 2000.0);

        // 4. Verify novel count ranking
        verify(redisUtil).zAdd("ranking:author:novelNum", author1.getUuid(), 10.0);
        verify(redisUtil).zAdd("ranking:author:novelNum", author2.getUuid(), 20.0);
    }

    @Test
    @DisplayName("updateAllRankings should call all individual update methods")
    void testUpdateAllRankings() {
        // We can't easily spy on private methods, but we can verify the mappers are called,
        // which implies the private methods were executed.
        when(novelMapper.selectAllNovelsForRanking()).thenReturn(Collections.emptyList());
        when(userMapper.selectAllUsersForRanking()).thenReturn(Collections.emptyList());
        when(novelMapper.selectAuthorsByRanking(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        // When
        rankingUpdateService.updateAllRankings();

        // Then
        verify(novelMapper).selectAllNovelsForRanking();
        verify(userMapper).selectAllUsersForRanking();
        verify(novelMapper).selectAuthorsByRanking(anyString(), anyInt(), anyInt());
    }


    // Helper methods for creating mock data
    private Novel createMockNovel(Integer id, Integer categoryId, Long viewCnt, Integer voteCnt) {
        Novel novel = new Novel();
        novel.setId(id);
        novel.setCategoryId(categoryId);
        novel.setViewCnt(viewCnt);
        novel.setVoteCnt(voteCnt);
        return novel;
    }

    private User createMockUser(UUID uuid, Float exp) {
        User user = new User();
        user.setUuid(uuid);
        user.setExp(exp);
        return user;
    }

    private AuthorResponseDTO createMockAuthor(String uuid, Integer novelNum, Long totalVoteCnt, Long totalViewCnt) {
        AuthorResponseDTO author = new AuthorResponseDTO();
        author.setUuid(uuid);
        author.setNovelNum(novelNum);
        author.setTotalVoteCnt(totalVoteCnt);
        author.setTotalViewCnt(totalViewCnt);
        return author;
    }
}