package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dao.VoteMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.*;
import com.yushan.backend.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteService Tests")
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

    private UUID userId;
    private Integer novelId;
    private User user;
    private Novel novel;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        novelId = 1;

        user = new User();
        user.setUuid(userId);
        user.setUsername("testuser");
        user.setYuan(10.0f);

        novel = new Novel();
        novel.setId(novelId);
        novel.setTitle("Test Novel");
        novel.setAuthorId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Test toggleVote - Success")
    void testToggleVoteSuccess() {
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(userMapper.selectByPrimaryKey(userId)).thenReturn(user);
        doNothing().when(novelService).incrementVoteCount(novelId);
        when(novelService.getNovelVoteCount(novelId)).thenReturn(1);
        doNothing().when(expService).addExp(eq(userId), eq(3f));
        doAnswer(invocation -> {
            Vote vote = invocation.getArgument(0);
            vote.setId(1);
            return 1;
        }).when(voteMapper).insertSelective(any(Vote.class));
        when(userMapper.updateByPrimaryKeySelective(any(User.class))).thenReturn(1);
        
        VoteResponseDTO result = voteService.toggleVote(novelId, userId);
        
        assertNotNull(result);
        assertEquals(novelId, result.getNovelId());
        verify(voteMapper).insertSelective(any(Vote.class));
        verify(expService).addExp(userId, 3f);
    }

    @Test
    @DisplayName("Test toggleVote - Cannot vote own novel")
    void testToggleVoteOwnNovel() {
        novel.setAuthorId(userId);
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        
        assertThrows(ValidationException.class, () -> {
            voteService.toggleVote(novelId, userId);
        });
    }

    @Test
    @DisplayName("Test toggleVote - Not enough yuan")
    void testToggleVoteNotEnoughYuan() {
        user.setYuan(0.0f);
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(userMapper.selectByPrimaryKey(userId)).thenReturn(user);
        
        assertThrows(ValidationException.class, () -> {
            voteService.toggleVote(novelId, userId);
        });
    }

    @Test
    @DisplayName("Test getUserVotes - Success with votes")
    void testGetUserVotesSuccess() {
        Vote vote = new Vote();
        vote.setId(1);
        vote.setUserId(userId);
        vote.setNovelId(novelId);
        vote.setCreateTime(new Date());
        
        when(voteMapper.countByUserId(userId)).thenReturn(1L);
        when(voteMapper.selectByUserIdWithPagination(userId, 0, 20)).thenReturn(Arrays.asList(vote));
        when(novelMapper.selectByIds(Collections.singletonList(novelId))).thenReturn(Arrays.asList(novel));
        
        PageResponseDTO<VoteUserResponseDTO> result = voteService.getUserVotes(userId, 0, 20);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());
    }

    @Test
    @DisplayName("Test getUserVotes - No votes")
    void testGetUserVotesEmpty() {
        when(voteMapper.countByUserId(userId)).thenReturn(0L);
        
        PageResponseDTO<VoteUserResponseDTO> result = voteService.getUserVotes(userId, 0, 20);
        
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0L, result.getTotalElements());
    }
}
