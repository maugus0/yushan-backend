package com.yushan.backend.service;

import com.yushan.backend.dao.SearchMapper;
import com.yushan.backend.dto.SearchRequestDTO;
import com.yushan.backend.dto.SearchResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Simple unit tests for SearchService.
 * 5 basic test cases to validate core functionality.
 */
@DisplayName("SearchService Tests")
class SearchServiceTest {

    @Mock
    private SearchMapper searchMapper;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test 1: Combined search returns response with novels and users")
    void testCombinedSearchReturnsResponse() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("test");
        request.setPage(1);
        request.setPageSize(10);

        when(searchMapper.searchNovels(anyString(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countNovels(anyString(), any())).thenReturn(0);
        when(searchMapper.searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countUsers(anyString())).thenReturn(0);

        // Act
        SearchResponseDTO result = searchService.search(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getNovels());
        assertNotNull(result.getUsers());
        assertEquals(0, result.getNovelCount());
        assertEquals(0, result.getUserCount());
    }

    @Test
    @DisplayName("Test 2: Novel search returns only novels")
    void testNovelSearchReturnsOnlyNovels() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("novel");
        request.setPage(1);
        request.setPageSize(10);

        when(searchMapper.searchNovels(anyString(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countNovels(anyString(), any())).thenReturn(5);

        // Act
        SearchResponseDTO result = searchService.searchNovels(request);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getNovelCount());
        assertEquals(0, result.getUserCount());
        verify(searchMapper, never()).searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("Test 3: User search returns only users")
    void testUserSearchReturnsOnlyUsers() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("user");
        request.setPage(1);
        request.setPageSize(10);

        when(searchMapper.searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countUsers(anyString())).thenReturn(3);

        // Act
        SearchResponseDTO result = searchService.searchUsers(request);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getNovelCount());
        assertEquals(3, result.getUserCount());
        verify(searchMapper, never()).searchNovels(anyString(), any(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("Test 4: Pagination offset is calculated correctly")
    void testPaginationOffsetCalculation() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("test");
        request.setPage(3);
        request.setPageSize(20);

        when(searchMapper.searchNovels(anyString(), any(), eq(40), eq(20), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countNovels(anyString(), any())).thenReturn(0);
        when(searchMapper.searchUsers(anyString(), eq(40), eq(20), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countUsers(anyString())).thenReturn(0);

        // Act
        SearchResponseDTO result = searchService.search(request);

        // Assert
        assertNotNull(result);
        verify(searchMapper).searchNovels(anyString(), any(), eq(40), eq(20), anyString(), anyString());
        verify(searchMapper).searchUsers(anyString(), eq(40), eq(20), anyString(), anyString());
    }

    @Test
    @DisplayName("Test 5: Category filter excludes user search")
    void testCategoryFilterExcludesUserSearch() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("fantasy");
        request.setCategory("Fantasy");
        request.setPage(1);
        request.setPageSize(10);

        when(searchMapper.searchNovels(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countNovels(anyString(), anyString())).thenReturn(10);

        // Act
        SearchResponseDTO result = searchService.search(request);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getNovelCount());
        assertEquals(0, result.getUserCount());
        verify(searchMapper).searchNovels(eq("fantasy"), eq("Fantasy"), anyInt(), anyInt(), anyString(), anyString());
        verify(searchMapper, never()).searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString());
        verify(searchMapper, never()).countUsers(anyString());
    }
    @Test
    @DisplayName("Test 6: Total pages calculation is correct")
    void testTotalPagesCalculation() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("test");
        request.setPage(1);
        request.setPageSize(10);

        when(searchMapper.searchNovels(anyString(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countNovels(anyString(), any())).thenReturn(23);
        when(searchMapper.searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countUsers(anyString())).thenReturn(17);

        // Act
        SearchResponseDTO result = searchService.search(request);

        // Assert
        assertNotNull(result);
        assertEquals(40L, result.getTotalResults()); // 23 + 17
        assertEquals(4, result.getTotalPages()); // ceil(40/10) = 4
        assertEquals(1, result.getCurrentPage());
    }

    @Test
    @DisplayName("Test 7: Empty category string searches both novels and users")
    void testEmptyCategorySearchesBoth() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("test");
        request.setCategory("");
        request.setPage(1);
        request.setPageSize(10);

        when(searchMapper.searchNovels(anyString(), eq(""), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countNovels(anyString(), eq(""))).thenReturn(5);
        when(searchMapper.searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countUsers(anyString())).thenReturn(3);

        // Act
        SearchResponseDTO result = searchService.search(request);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getNovelCount());
        assertEquals(3, result.getUserCount());
        verify(searchMapper).searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString());
        verify(searchMapper).countUsers(anyString());
    }

    @Test
    @DisplayName("Test 8: Novel search calculates total pages with single page")
    void testNovelSearchSinglePage() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("novel");
        request.setPage(1);
        request.setPageSize(20);

        when(searchMapper.searchNovels(anyString(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countNovels(anyString(), any())).thenReturn(15);

        // Act
        SearchResponseDTO result = searchService.searchNovels(request);

        // Assert
        assertNotNull(result);
        assertEquals(15, result.getNovelCount());
        assertEquals(1, result.getTotalPages()); // ceil(15/20) = 1
        assertEquals(15L, result.getTotalResults());
    }

    @Test
    @DisplayName("Test 9: User search calculates total pages with multiple pages")
    void testUserSearchMultiplePages() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("user");
        request.setPage(2);
        request.setPageSize(15);

        when(searchMapper.searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(searchMapper.countUsers(anyString())).thenReturn(47);

        // Act
        SearchResponseDTO result = searchService.searchUsers(request);

        // Assert
        assertNotNull(result);
        assertEquals(47, result.getUserCount());
        assertEquals(4, result.getTotalPages()); // ceil(47/15) = 4
        assertEquals(2, result.getCurrentPage());
        assertEquals(47L, result.getTotalResults());
    }

    @Test
    @DisplayName("Test 10: Combined search with actual novel and user data")
    void testCombinedSearchWithData() {
        // Arrange
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("test");
        request.setPage(1);
        request.setPageSize(10);

        List<Novel> novels = new ArrayList<>();
        Novel novel = new Novel();
        novel.setId(1);
        novel.setTitle("Test Novel");
        novels.add(novel);

        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUuid(java.util.UUID.randomUUID());
        user.setUsername("testuser");
        users.add(user);

        when(searchMapper.searchNovels(anyString(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(novels);
        when(searchMapper.countNovels(anyString(), any())).thenReturn(1);
        when(searchMapper.searchUsers(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(users);
        when(searchMapper.countUsers(anyString())).thenReturn(1);

        // Act
        SearchResponseDTO result = searchService.search(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getNovels().size());
        assertEquals(1, result.getUsers().size());
        assertEquals("Test Novel", result.getNovels().get(0).getTitle());
        assertEquals("testuser", result.getUsers().get(0).getUsername());
        assertEquals(1, result.getNovelCount());
        assertEquals(1, result.getUserCount());
        assertEquals(2L, result.getTotalResults());
    }
}
