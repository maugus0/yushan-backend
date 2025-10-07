package com.yushan.backend.controller;

import com.yushan.backend.dto.VoteResponseDTO;
import com.yushan.backend.dto.VoteStatsResponseDTO;
import com.yushan.backend.dto.VoteStatusResponseDTO;
import com.yushan.backend.service.VoteService;
import com.yushan.backend.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VoteController
 */
@ExtendWith(MockitoExtension.class)
public class VoteControllerTest {

    @Mock
    private VoteService voteService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private CustomUserDetailsService.CustomUserDetails userDetails;

    @InjectMocks
    private VoteController voteController;

    private UUID testUserId;
    private Integer testNovelId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testNovelId = 123;
    }

    @Test
    void toggleVote_Success() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());
        
        VoteResponseDTO mockResponse = new VoteResponseDTO(testNovelId, 5, true);
        when(voteService.toggleVote(eq(testNovelId), eq(testUserId))).thenReturn(mockResponse);

        // When
        var result = voteController.toggleVote(testNovelId, authentication);

        // Then
        assertNotNull(result);
        assertEquals("Voted successfully", result.getMessage());
        assertEquals(mockResponse, result.getData());
        verify(voteService).toggleVote(testNovelId, testUserId);
    }

    @Test
    void toggleVote_Unvote_Success() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());
        
        VoteResponseDTO mockResponse = new VoteResponseDTO(testNovelId, 4, false);
        when(voteService.toggleVote(eq(testNovelId), eq(testUserId))).thenReturn(mockResponse);

        // When
        var result = voteController.toggleVote(testNovelId, authentication);

        // Then
        assertNotNull(result);
        assertEquals("Vote removed successfully", result.getMessage());
        assertEquals(mockResponse, result.getData());
        verify(voteService).toggleVote(testNovelId, testUserId);
    }

    @Test
    void getVoteStats_Success() {
        // Given
        VoteStatsResponseDTO mockResponse = new VoteStatsResponseDTO(testNovelId, 10);
        when(voteService.getVoteStats(testNovelId)).thenReturn(mockResponse);

        // When
        var result = voteController.getVoteStats(testNovelId);

        // Then
        assertNotNull(result);
        assertEquals("Vote statistics retrieved", result.getMessage());
        assertEquals(mockResponse, result.getData());
        verify(voteService).getVoteStats(testNovelId);
    }

    @Test
    void getUserVoteStatus_Success() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());
        
        Date votedAt = new Date();
        VoteStatusResponseDTO mockResponse = new VoteStatusResponseDTO(testNovelId, true, votedAt);
        when(voteService.getUserVoteStatus(eq(testNovelId), eq(testUserId))).thenReturn(mockResponse);

        // When
        var result = voteController.getUserVoteStatus(testNovelId, authentication);

        // Then
        assertNotNull(result);
        assertEquals("Vote status retrieved", result.getMessage());
        assertEquals(mockResponse, result.getData());
        verify(voteService).getUserVoteStatus(testNovelId, testUserId);
    }

    @Test
    void getUserVoteStatus_NotVoted() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());
        
        VoteStatusResponseDTO mockResponse = new VoteStatusResponseDTO(testNovelId, false, null);
        when(voteService.getUserVoteStatus(eq(testNovelId), eq(testUserId))).thenReturn(mockResponse);

        // When
        var result = voteController.getUserVoteStatus(testNovelId, authentication);

        // Then
        assertNotNull(result);
        assertEquals("Vote status retrieved", result.getMessage());
        assertEquals(mockResponse, result.getData());
        assertFalse(result.getData().getHasVoted());
        assertNull(result.getData().getVotedAt());
    }

    @Test
    void getUserIdFromAuthentication_NullAuthentication() {
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            voteController.getUserVoteStatus(testNovelId, null);
        });
    }

    @Test
    void getUserIdFromAuthentication_InvalidPrincipal() {
        // Given
        when(authentication.getPrincipal()).thenReturn("invalid");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            voteController.getUserVoteStatus(testNovelId, authentication);
        });
    }

    @Test
    void getUserIdFromAuthentication_NullUserId() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            voteController.getUserVoteStatus(testNovelId, authentication);
        });
    }
}
