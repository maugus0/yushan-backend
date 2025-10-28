package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Search DTO Tests")
class SearchDTOTest {

    @Test
    @DisplayName("Test SearchRequestDTO")
    void testSearchRequestDTO() {
        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword("test query");
        dto.setCategory("Fantasy");
        dto.setPage(2);
        dto.setPageSize(20);
        dto.setSortBy("created_at");
        dto.setSortOrder("DESC");
        
        assertEquals("test query", dto.getKeyword());
        assertEquals("Fantasy", dto.getCategory());
        assertEquals(2, dto.getPage());
        assertEquals(20, dto.getPageSize());
        assertEquals("created_at", dto.getSortBy());
        assertEquals("DESC", dto.getSortOrder());
    }

    @Test
    @DisplayName("Test SearchResponseDTO with novels and users")
    void testSearchResponseDTO() {
        NovelDetailResponseDTO novel = new NovelDetailResponseDTO();
        novel.setTitle("Test Novel");
        novel.setSynopsis("Synopsis");
        
        UserProfileResponseDTO user = new UserProfileResponseDTO();
        user.setUsername("testuser");
        user.setIsAuthor(true);
        
        List<NovelDetailResponseDTO> novels = Arrays.asList(novel);
        List<UserProfileResponseDTO> users = Arrays.asList(user);
        
        SearchResponseDTO dto = new SearchResponseDTO(novels, 100, users, 50, 1, 10, 150L);
        
        assertEquals(1, dto.getNovels().size());
        assertEquals(100, dto.getNovelCount());
        assertEquals(1, dto.getUsers().size());
        assertEquals(50, dto.getUserCount());
        assertEquals(1, dto.getCurrentPage());
        assertEquals(10, dto.getTotalPages());
        assertEquals(150L, dto.getTotalResults());
        
        // Test helper methods
        assertTrue(dto.hasNextPage());
        assertTrue(dto.hasResults());
    }

    @Test
    @DisplayName("Test SearchResponseDTO with empty results")
    void testSearchResponseDTOEmpty() {
        SearchResponseDTO dto = new SearchResponseDTO(
            Arrays.asList(), 0, Arrays.asList(), 0, 1, 1, 0L
        );
        
        assertEquals(0, dto.getNovels().size());
        assertEquals(0, dto.getNovelCount());
        assertEquals(0, dto.getUsers().size());
        assertEquals(0, dto.getUserCount());
        assertFalse(dto.hasNextPage());
        assertFalse(dto.hasResults());
    }

    @Test
    @DisplayName("Test SearchResponseDTO defensive copying")
    void testSearchResponseDTODefensiveCopying() {
        NovelDetailResponseDTO novel = new NovelDetailResponseDTO();
        novel.setTitle("Novel 1");
        
        UserProfileResponseDTO user = new UserProfileResponseDTO();
        user.setUsername("user1");
        
        List<NovelDetailResponseDTO> novels = new java.util.ArrayList<>(Arrays.asList(novel));
        List<UserProfileResponseDTO> users = new java.util.ArrayList<>(Arrays.asList(user));
        
        SearchResponseDTO dto = new SearchResponseDTO(novels, 1, users, 1, 0, 1, 2L);
        
        // Test defensive copying for novels
        novels.add(new NovelDetailResponseDTO());
        assertEquals(1, dto.getNovels().size());
        
        // Test defensive copying for users
        users.add(new UserProfileResponseDTO());
        assertEquals(1, dto.getUsers().size());
        
        // Test setter defensive copying
        List<NovelDetailResponseDTO> novels2 = new java.util.ArrayList<>(Arrays.asList(novel));
        dto.setNovels(novels2);
        novels2.add(new NovelDetailResponseDTO());
        assertEquals(1, dto.getNovels().size());
        
        List<UserProfileResponseDTO> users2 = new java.util.ArrayList<>(Arrays.asList(user));
        dto.setUsers(users2);
        users2.add(new UserProfileResponseDTO());
        assertEquals(1, dto.getUsers().size());
        
        // Test unmodifiable list from getter
        assertThrows(Exception.class, () -> {
            dto.getNovels().add(new NovelDetailResponseDTO());
        });
        
        assertThrows(Exception.class, () -> {
            dto.getUsers().add(new UserProfileResponseDTO());
        });
        
        // Test helper methods
        dto.setCurrentPage(5);
        dto.setTotalPages(10);
        assertTrue(dto.hasNextPage());
        assertTrue(dto.hasPreviousPage());
        
        dto.setCurrentPage(1);
        assertFalse(dto.hasPreviousPage());
        
        dto.setCurrentPage(10);
        assertFalse(dto.hasNextPage());
        
        // Test hasResults with null lists
        SearchResponseDTO dto2 = new SearchResponseDTO();
        dto2.setNovels(null);
        dto2.setUsers(null);
        assertFalse(dto2.hasResults());
        
        // Test hasResults with empty lists
        dto2.setNovels(new java.util.ArrayList<>());
        dto2.setUsers(new java.util.ArrayList<>());
        assertFalse(dto2.hasResults());
        
        // Test with null counts
        dto2.setNovelCount(null);
        dto2.setUserCount(null);
        assertNull(dto2.getNovelCount());
        assertNull(dto2.getUserCount());
        
        // Test equals, hashCode, and canEqual methods for SearchRequestDTO
        SearchRequestDTO search1 = new SearchRequestDTO();
        search1.setKeyword("test");
        search1.setCategory("fantasy");
        
        SearchRequestDTO search2 = new SearchRequestDTO();
        search2.setKeyword("test");
        search2.setCategory("fantasy");
        
        assertEquals(search1, search2);
        assertEquals(search1.hashCode(), search2.hashCode());
        assertNotEquals(search1, null);
        assertEquals(search1, search1);
        assertTrue(search1.canEqual(search2));
        
        // Test SearchResponseDTO equals, hashCode, canEqual
        SearchResponseDTO searchResp1 = new SearchResponseDTO();
        searchResp1.setNovelCount(10);
        searchResp1.setUserCount(5);
        
        SearchResponseDTO searchResp2 = new SearchResponseDTO();
        searchResp2.setNovelCount(10);
        searchResp2.setUserCount(5);
        
        assertEquals(searchResp1, searchResp2);
        assertEquals(searchResp1.hashCode(), searchResp2.hashCode());
        assertNotEquals(searchResp1, null);
        assertEquals(searchResp1, searchResp1);
        assertTrue(searchResp1.canEqual(searchResp2));
    }
}

