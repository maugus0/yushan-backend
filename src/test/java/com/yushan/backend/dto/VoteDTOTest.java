package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vote DTO Tests")
class VoteDTOTest {

    @Test
    @DisplayName("Test VoteResponseDTO")
    void testVoteResponseDTO() {
        VoteResponseDTO dto = new VoteResponseDTO(123, 50, 100.0f);
        
        assertEquals(123, dto.getNovelId());
        assertEquals(50, dto.getVoteCount());
        assertEquals(100.0f, dto.getRemainedYuan());
        
        // Test with different values
        VoteResponseDTO dto2 = new VoteResponseDTO(456, 100, 200.5f);
        assertEquals(456, dto2.getNovelId());
        assertEquals(100, dto2.getVoteCount());
        assertEquals(200.5f, dto2.getRemainedYuan());
    }

    @Test
    @DisplayName("Test AuthorResponseDTO basic")
    void testAuthorResponseDTOBasic() {
        AuthorResponseDTO dto = new AuthorResponseDTO();
        dto.setUuid("user-uuid");
        dto.setUsername("testuser");
        dto.setAvatarUrl("http://example.com/avatar.jpg");
        dto.setNovelNum(5);
        dto.setTotalVoteCnt(100L);
        dto.setTotalViewCnt(1000L);
        
        assertEquals("user-uuid", dto.getUuid());
        assertEquals("testuser", dto.getUsername());
        assertEquals("http://example.com/avatar.jpg", dto.getAvatarUrl());
        assertEquals(5, dto.getNovelNum());
        assertEquals(100L, dto.getTotalVoteCnt());
        assertEquals(1000L, dto.getTotalViewCnt());
        
        // Test with null values
        AuthorResponseDTO dto2 = new AuthorResponseDTO();
        dto2.setAvatarUrl(null);
        dto2.setTotalViewCnt(null);
        assertNull(dto2.getAvatarUrl());
        assertNull(dto2.getTotalViewCnt());
        assertEquals(0L, dto2.getTotalVoteCnt()); // long primitive defaults to 0
    }

    @Test
    @DisplayName("Test BatchRequestDTO")
    void testBatchRequestDTO() {
        BatchRequestDTO dto = new BatchRequestDTO();
        java.util.List<Integer> ids = new java.util.ArrayList<>(Arrays.asList(1, 2, 3));
        dto.setIds(ids);
        
        assertNotNull(dto.getIds());
        assertEquals(3, dto.getIds().size());
        assertEquals(1, dto.getIds().get(0));
        assertEquals(2, dto.getIds().get(1));
        assertEquals(3, dto.getIds().get(2));
        
        // Test defensive copying
        ids.add(4);
        assertEquals(3, dto.getIds().size());
        
        java.util.List<Integer> retrieved = dto.getIds();
        assertNotSame(ids, retrieved);
        
        // Test null
        dto.setIds(null);
        assertNull(dto.getIds());
    }

    @Test
    @DisplayName("Test VoteUserResponseDTO")
    void testVoteUserResponseDTO() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        VoteUserResponseDTO dto = new VoteUserResponseDTO(1, 100, "Test Novel", now);
        
        assertEquals(1, dto.getId());
        assertEquals(100, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals(now, dto.getVotedTime());
        
        // Test default constructor
        VoteUserResponseDTO dto2 = new VoteUserResponseDTO();
        dto2.setId(2);
        dto2.setNovelId(200);
        dto2.setNovelTitle("Another Novel");
        dto2.setVotedTime(now);
        
        assertEquals(2, dto2.getId());
        assertEquals(200, dto2.getNovelId());
        assertEquals("Another Novel", dto2.getNovelTitle());
        assertEquals(now, dto2.getVotedTime());
        
        // Test with specific LocalDateTime
        java.time.LocalDateTime votedTime = java.time.LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        VoteUserResponseDTO dto3 = new VoteUserResponseDTO(3, 300, "Novel 3", votedTime);
        assertEquals(3, dto3.getId());
        assertEquals(300, dto3.getNovelId());
        assertEquals("Novel 3", dto3.getNovelTitle());
        assertEquals(votedTime, dto3.getVotedTime());
    }
    
    @Test
    @DisplayName("Test AuthorResponseDTO")
    void testAuthorResponseDTO() {
        AuthorResponseDTO dto = new AuthorResponseDTO();
        dto.setUuid("author-uuid");
        dto.setUsername("author");
        dto.setAvatarUrl("http://avatar.url");
        dto.setNovelNum(10);
        dto.setTotalVoteCnt(5000L);
        dto.setTotalViewCnt(100000L);
        
        assertEquals("author-uuid", dto.getUuid());
        assertEquals("author", dto.getUsername());
        assertEquals("http://avatar.url", dto.getAvatarUrl());
        assertEquals(10, dto.getNovelNum());
        assertEquals(5000L, dto.getTotalVoteCnt());
        assertEquals(100000L, dto.getTotalViewCnt());
        
        // Test with different values
        dto.setUuid("another-uuid");
        dto.setUsername("another-author");
        dto.setAvatarUrl("http://another.avatar");
        dto.setNovelNum(20);
        dto.setTotalVoteCnt(10000L);
        dto.setTotalViewCnt(200000L);
        
        assertEquals("another-uuid", dto.getUuid());
        assertEquals("another-author", dto.getUsername());
        assertEquals("http://another.avatar", dto.getAvatarUrl());
        assertEquals(20, dto.getNovelNum());
        assertEquals(10000L, dto.getTotalVoteCnt());
        assertEquals(200000L, dto.getTotalViewCnt());
        
        // Test with null values
        dto.setUuid(null);
        dto.setUsername(null);
        dto.setAvatarUrl(null);
        dto.setNovelNum(null);
        dto.setTotalViewCnt(null);
        
        assertNull(dto.getUuid());
        assertNull(dto.getUsername());
        assertNull(dto.getAvatarUrl());
        assertNull(dto.getNovelNum());
        assertNull(dto.getTotalViewCnt());
        
        // Test equals, hashCode, and canEqual methods for VoteResponseDTO
        VoteResponseDTO vote1 = new VoteResponseDTO(1, 10, 100.0f);
        VoteResponseDTO vote2 = new VoteResponseDTO(1, 10, 100.0f);
        
        assertEquals(vote1, vote2);
        assertEquals(vote1.hashCode(), vote2.hashCode());
        assertNotEquals(vote1, null);
        assertEquals(vote1, vote1);
        assertTrue(vote1.canEqual(vote2));
        
        // Test VoteUserResponseDTO equals, hashCode, canEqual
        java.time.LocalDateTime time = java.time.LocalDateTime.now();
        VoteUserResponseDTO voteUser1 = new VoteUserResponseDTO(1, 100, "Novel", time);
        VoteUserResponseDTO voteUser2 = new VoteUserResponseDTO(1, 100, "Novel", time);
        
        assertEquals(voteUser1, voteUser2);
        assertEquals(voteUser1.hashCode(), voteUser2.hashCode());
        assertNotEquals(voteUser1, null);
        assertEquals(voteUser1, voteUser1);
        assertTrue(voteUser1.canEqual(voteUser2));
        
        // Test BatchRequestDTO equals, hashCode, canEqual
        BatchRequestDTO batch1 = new BatchRequestDTO();
        java.util.List<Integer> ids1 = new java.util.ArrayList<>(Arrays.asList(1, 2, 3));
        batch1.setIds(ids1);
        
        BatchRequestDTO batch2 = new BatchRequestDTO();
        java.util.List<Integer> ids2 = new java.util.ArrayList<>(Arrays.asList(1, 2, 3));
        batch2.setIds(ids2);
        
        assertEquals(batch1, batch2);
        assertEquals(batch1.hashCode(), batch2.hashCode());
        assertNotEquals(batch1, null);
        assertEquals(batch1, batch1);
        assertTrue(batch1.canEqual(batch2));
        
        // Test equals, hashCode, and canEqual methods for AuthorResponseDTO (cover remaining methods)
        AuthorResponseDTO author1 = new AuthorResponseDTO();
        author1.setUuid("uuid1");
        author1.setUsername("author1");
        author1.setAvatarUrl("avatar1");
        author1.setNovelNum(5);
        author1.setTotalViewCnt(1000L);
        
        AuthorResponseDTO author2 = new AuthorResponseDTO();
        author2.setUuid("uuid1");
        author2.setUsername("author1");
        author2.setAvatarUrl("avatar1");
        author2.setNovelNum(5);
        author2.setTotalViewCnt(1000L);
        
        assertEquals(author1, author2);
        assertEquals(author1.hashCode(), author2.hashCode());
        assertNotEquals(author1, null);
        assertEquals(author1, author1);
        assertTrue(author1.canEqual(author2));
        
        // Test with different values
        author2.setUuid("uuid2");
        assertNotEquals(author1, author2);
        
        // Test with null values
        AuthorResponseDTO author3 = new AuthorResponseDTO();
        author3.setUuid(null);
        author3.setUsername(null);
        author3.setAvatarUrl(null);
        author3.setNovelNum(null);
        author3.setTotalViewCnt(null);
        
        assertNull(author3.getUuid());
        assertNull(author3.getUsername());
        assertNull(author3.getAvatarUrl());
        assertNull(author3.getNovelNum());
        assertNull(author3.getTotalViewCnt());
        
        // Test setTotalVoteCnt with long primitive
        author3.setTotalVoteCnt(500L);
        assertEquals(500L, author3.getTotalVoteCnt());
    }
}

