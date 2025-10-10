package com.yushan.backend.service;

import com.yushan.backend.dao.VoteMapper;
import com.yushan.backend.dto.VoteResponseDTO;
import com.yushan.backend.dto.VoteStatsResponseDTO;
import com.yushan.backend.dto.VoteStatusResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Vote;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VoteService
 */
public class VoteServiceTest {

    private VoteMapper voteMapper;
    private NovelService novelService;
    private VoteService voteService;
    private EXPService expService;

    @BeforeEach
    void setUp() {
        voteMapper = Mockito.mock(VoteMapper.class);
        novelService = Mockito.mock(NovelService.class);
        expService = Mockito.mock(EXPService.class);
        voteService = new VoteService();
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field voteMapperField = VoteService.class.getDeclaredField("voteMapper");
            voteMapperField.setAccessible(true);
            voteMapperField.set(voteService, voteMapper);
            
            java.lang.reflect.Field novelServiceField = VoteService.class.getDeclaredField("novelService");
            novelServiceField.setAccessible(true);
            novelServiceField.set(voteService, novelService);

            java.lang.reflect.Field expServiceField = VoteService.class.getDeclaredField("expService");
            expServiceField.setAccessible(true);
            expServiceField.set(voteService, expService);

        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }
    }

    @Test
    void testToggleVote_FirstVote_Success() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setAuthorId(authorId);
        novel.setVoteCnt(5);
        
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(voteMapper.selectActiveByUserAndNovel(userId, novelId)).thenReturn(null);
        when(novelService.getNovelVoteCount(novelId)).thenReturn(6);
        
        // Act
        VoteResponseDTO result = voteService.toggleVote(novelId, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(novelId, result.getNovelId());
        assertEquals(6, result.getVoteCount());
        assertTrue(result.getUserVoted());
        
        verify(voteMapper).insertSelective(any(Vote.class));
        verify(novelService).incrementVoteCount(novelId);
    }

    @Test
    void testToggleVote_Unvote_Success() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setAuthorId(authorId);
        novel.setVoteCnt(6);
        
        Vote existingVote = new Vote();
        existingVote.setUserId(userId);
        existingVote.setNovelId(novelId);
        existingVote.setIsActive(true);
        existingVote.setCreateTime(new Date());
        
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        when(voteMapper.selectActiveByUserAndNovel(userId, novelId)).thenReturn(existingVote);
        when(novelService.getNovelVoteCount(novelId)).thenReturn(5);
        
        // Act
        VoteResponseDTO result = voteService.toggleVote(novelId, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(novelId, result.getNovelId());
        assertEquals(5, result.getVoteCount());
        assertFalse(result.getUserVoted());
        
        verify(voteMapper).deactivateVote(userId, novelId);
        verify(novelService).decrementVoteCount(novelId);
    }

    @Test
    void testToggleVote_SelfVote_ThrowsException() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setAuthorId(userId); // Same as voter
        
        when(novelService.getNovelEntity(novelId)).thenReturn(novel);
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            voteService.toggleVote(novelId, userId);
        });
        
        assertEquals("Cannot vote your own novel", exception.getMessage());
        verify(voteMapper, never()).insertSelective(any());
        verify(voteMapper, never()).deactivateVote(any(), any());
    }


    @Test
    void testToggleVote_NovelNotFound_ThrowsException() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        
        when(novelService.getNovelEntity(novelId)).thenThrow(new ResourceNotFoundException("Novel not found"));
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            voteService.toggleVote(novelId, userId);
        });
        
        assertEquals("Novel not found", exception.getMessage());
    }

    @Test
    void testGetVoteStats_Success() {
        // Arrange
        Integer novelId = 1;
        Integer voteCount = 42;
        
        when(novelService.getNovelVoteCount(novelId)).thenReturn(voteCount);
        
        // Act
        VoteStatsResponseDTO result = voteService.getVoteStats(novelId);
        
        // Assert
        assertNotNull(result);
        assertEquals(novelId, result.getNovelId());
        assertEquals(voteCount, result.getTotalVotes());
        
        verify(novelService).getNovelVoteCount(novelId);
    }

    @Test
    void testGetUserVoteStatus_UserHasVoted() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        Date voteTime = new Date();
        
        Vote existingVote = new Vote();
        existingVote.setUserId(userId);
        existingVote.setNovelId(novelId);
        existingVote.setIsActive(true);
        existingVote.setCreateTime(voteTime);
        
        when(novelService.getNovelVoteCount(novelId)).thenReturn(42);
        when(voteMapper.selectActiveByUserAndNovel(userId, novelId)).thenReturn(existingVote);
        
        // Act
        VoteStatusResponseDTO result = voteService.getUserVoteStatus(novelId, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(novelId, result.getNovelId());
        assertTrue(result.getHasVoted());
        assertEquals(voteTime, result.getVotedAt());
    }

    @Test
    void testGetUserVoteStatus_UserHasNotVoted() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        
        when(novelService.getNovelVoteCount(novelId)).thenReturn(42);
        when(voteMapper.selectActiveByUserAndNovel(userId, novelId)).thenReturn(null);
        
        // Act
        VoteStatusResponseDTO result = voteService.getUserVoteStatus(novelId, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(novelId, result.getNovelId());
        assertFalse(result.getHasVoted());
        assertNull(result.getVotedAt());
    }

    @Test
    void testGetUserVoteStatus_NovelNotFound_ThrowsException() {
        // Arrange
        Integer novelId = 1;
        UUID userId = UUID.randomUUID();
        
        when(novelService.getNovelVoteCount(novelId)).thenThrow(new ResourceNotFoundException("Novel not found"));
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            voteService.getUserVoteStatus(novelId, userId);
        });
        
        assertEquals("Novel not found", exception.getMessage());
    }
}
