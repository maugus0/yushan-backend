package com.yushan.backend.service;

import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.dto.NovelDetailResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RankingServiceTest {

    @Mock
    private NovelMapper novelMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RankingService rankingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void rankNovel_ShouldReturnPagedResponse() {
        // Given
        List<Novel> mockNovels = Arrays.asList(createMockNovel(1), createMockNovel(2));
        when(novelMapper.selectNovelsByRanking(any(), anyString(), anyInt(), anyInt()))
                .thenReturn(mockNovels);
        when(novelMapper.countNovelsByRanking(any())).thenReturn(2L);

        // When
        PageResponseDTO<NovelDetailResponseDTO> result = rankingService.rankNovel(0, 10, "view", 1, "overall");

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getSize());
        verify(novelMapper).selectNovelsByRanking(1, "view", 0, 10);
        verify(novelMapper).countNovelsByRanking(1);
    }

    @Test
    void rankNovel_WhenOffsetExceedsMaxRecords_ShouldReturnEmptyResponse() {
        // When
        PageResponseDTO<NovelDetailResponseDTO> result = rankingService.rankNovel(10, 20, "view", null, "overall");

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(10, result.getCurrentPage());
        assertEquals(20, result.getSize());
    }

    @Test
    void rankUser_ShouldReturnPagedResponse() {
        // Given
        List<User> mockUsers = Arrays.asList(createMockUser(UUID.randomUUID()), createMockUser(UUID.randomUUID()));
        when(userMapper.selectUsersByRanking(anyInt(), anyInt())).thenReturn(mockUsers);
        when(userMapper.countAllUsers()).thenReturn(2L);

        // When
        PageResponseDTO<UserProfileResponseDTO> result = rankingService.rankUser(0, 10, "overall");

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getSize());
        verify(userMapper).selectUsersByRanking(0, 10);
        verify(userMapper).countAllUsers();
    }

    @Test
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
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getSize());
        verify(novelMapper).selectAuthorsByRanking("vote", 0, 10);
        verify(userMapper).countAllAuthors();
    }

    private Novel createMockNovel(Integer id) {
        Novel novel = new Novel();
        novel.setId(id);
        novel.setTitle("Test Novel " + id);
        novel.setViewCnt(100L);
        novel.setVoteCnt(50);
        novel.setCoverImgUrl("cover.jpg");
        novel.setCategoryId(1);
        return novel;
    }

    private User createMockUser(UUID id) {
        User user = new User();
        user.setUuid(id);
        user.setUuid(UUID.randomUUID());
        user.setUsername("testuser" + id);
        user.setExp(100f);
        user.setLevel(1);
        user.setAvatarUrl("avatar.jpg");
        return user;
    }
}
