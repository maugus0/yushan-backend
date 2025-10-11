package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dao.VoteMapper;
import com.yushan.backend.dto.VoteResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import com.yushan.backend.entity.Vote;
import com.yushan.backend.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoteServiceTest {

    @Mock
    private VoteMapper voteMapper;

    @Mock
    private NovelService novelService;

    @Mock
    private EXPService expService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private NovelMapper novelMapper;

    @InjectMocks
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toggleVote_Success() {
        // Given
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();

        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setAuthorId(UUID.randomUUID()); // Different from userId

        User user = new User();
        user.setUuid(userId);
        user.setYuan(5f);

        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(userMapper.selectByPrimaryKey(userId)).thenReturn(user);

        // When
        VoteResponseDTO result = voteService.toggleVote(novelId, userId);

        // Then
        assertNotNull(result);
        assertEquals(novelId, result.getNovelId());
        assertEquals(4, result.getRemainedYuan()); // 5 - 1 = 4

        verify(voteMapper, times(1)).insertSelective(any(Vote.class));
        verify(userMapper, times(1)).updateByPrimaryKeySelective(user);
        verify(novelService, times(1)).incrementVoteCount(novelId);
        verify(expService, times(1)).addExp(userId, 3f);
    }

    @Test
    void toggleVote_ThrowsException_WhenUserIsAuthor() {
        // Given
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();

        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setAuthorId(userId); // Same as userId

        when(novelService.getNovelEntity(novelId)).thenReturn(novel);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> voteService.toggleVote(novelId, userId));

        assertEquals("Cannot vote your own novel", exception.getMessage());

        verify(voteMapper, never()).insertSelective(any(Vote.class));
        verify(userMapper, never()).updateByPrimaryKeySelective(any(User.class));
    }

    @Test
    void toggleVote_ThrowsException_WhenNotEnoughYuan() {
        // Given
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();

        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setAuthorId(UUID.randomUUID()); // Different from userId

        User user = new User();
        user.setUuid(userId);
        user.setYuan(0f); // Not enough yuan

        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(userMapper.selectByPrimaryKey(userId)).thenReturn(user);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> voteService.toggleVote(novelId, userId));

        assertEquals("Not enough yuan", exception.getMessage());

        verify(voteMapper, never()).insertSelective(any(Vote.class));
        verify(userMapper, never()).updateByPrimaryKeySelective(any(User.class));
    }
}
