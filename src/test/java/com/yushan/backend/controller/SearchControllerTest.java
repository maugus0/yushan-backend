package com.yushan.backend.controller;

import com.yushan.backend.dto.SearchRequestDTO;
import com.yushan.backend.dto.SearchResponseDTO;
import com.yushan.backend.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Simple unit tests for SearchController.
 * 5 basic test cases to validate core functionality.
 */
@DisplayName("SearchController Tests")
class SearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    @DisplayName("Test 1: Combined search endpoint returns 200 OK")
    void testCombinedSearchReturns200() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 0, new ArrayList<>(), 0, 1, 0, 0L
        );
        when(searchService.search(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/search")
                        .param("keyword", "test"))
                .andExpect(status().isOk());
        
        verify(searchService, times(1)).search(any(SearchRequestDTO.class));
    }

    @Test
    @DisplayName("Test 2: Novel search endpoint returns 200 OK")
    void testNovelSearchReturns200() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 0, new ArrayList<>(), 0, 1, 0, 0L
        );
        when(searchService.searchNovels(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/search/novels")
                        .param("keyword", "novel"))
                .andExpect(status().isOk());
        
        verify(searchService, times(1)).searchNovels(any(SearchRequestDTO.class));
    }

    @Test
    @DisplayName("Test 3: User search endpoint returns 200 OK")
    void testUserSearchReturns200() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 0, new ArrayList<>(), 0, 1, 0, 0L
        );
        when(searchService.searchUsers(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/search/users")
                        .param("keyword", "user"))
                .andExpect(status().isOk());
        
        verify(searchService, times(1)).searchUsers(any(SearchRequestDTO.class));
    }

    @Test
    @DisplayName("Test 4: Combined search passes correct keyword to service")
    void testCombinedSearchPassesKeyword() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 0, new ArrayList<>(), 0, 1, 0, 0L
        );
        when(searchService.search(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act
        mockMvc.perform(get("/api/search")
                        .param("keyword", "fantasy"))
                .andExpect(status().isOk());

        // Assert
        ArgumentCaptor<SearchRequestDTO> captor = ArgumentCaptor.forClass(SearchRequestDTO.class);
        verify(searchService).search(captor.capture());
        assertEquals("fantasy", captor.getValue().getKeyword());
    }

    @Test
    @DisplayName("Test 5: Search with pagination parameters")
    void testSearchWithPagination() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 0, new ArrayList<>(), 0, 2, 5, 50L
        );
        when(searchService.search(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act
        mockMvc.perform(get("/api/search")
                        .param("keyword", "test")
                        .param("page", "2")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(2))
                .andExpect(jsonPath("$.totalPages").value(5));

        // Assert
        ArgumentCaptor<SearchRequestDTO> captor = ArgumentCaptor.forClass(SearchRequestDTO.class);
        verify(searchService).search(captor.capture());
        assertEquals(2, captor.getValue().getPage());
        assertEquals(20, captor.getValue().getPageSize());
    }
    @Test
    @DisplayName("Test 6: Combined search with category filter")
    void testCombinedSearchWithCategory() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 5, new ArrayList<>(), 0, 1, 1, 5L
        );
        when(searchService.search(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act
        mockMvc.perform(get("/api/search")
                        .param("keyword", "adventure")
                        .param("category", "Fantasy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.novelCount").value(5))
                .andExpect(jsonPath("$.userCount").value(0));

        // Assert
        ArgumentCaptor<SearchRequestDTO> captor = ArgumentCaptor.forClass(SearchRequestDTO.class);
        verify(searchService).search(captor.capture());
        assertEquals("adventure", captor.getValue().getKeyword());
        assertEquals("Fantasy", captor.getValue().getCategory());
    }

    @Test
    @DisplayName("Test 7: Novel search with sorting parameters")
    void testNovelSearchWithSorting() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 3, new ArrayList<>(), 0, 1, 1, 3L
        );
        when(searchService.searchNovels(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act
        mockMvc.perform(get("/api/search/novels")
                        .param("keyword", "romance")
                        .param("sortBy", "title")
                        .param("sortOrder", "ASC"))
                .andExpect(status().isOk());

        // Assert
        ArgumentCaptor<SearchRequestDTO> captor = ArgumentCaptor.forClass(SearchRequestDTO.class);
        verify(searchService).searchNovels(captor.capture());
        assertEquals("title", captor.getValue().getSortBy());
        assertEquals("ASC", captor.getValue().getSortOrder());
    }

    @Test
    @DisplayName("Test 8: User search without keyword")
    void testUserSearchWithoutKeyword() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 0, new ArrayList<>(), 10, 1, 1, 10L
        );
        when(searchService.searchUsers(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act
        mockMvc.perform(get("/api/search/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCount").value(10));

        // Assert
        ArgumentCaptor<SearchRequestDTO> captor = ArgumentCaptor.forClass(SearchRequestDTO.class);
        verify(searchService).searchUsers(captor.capture());
        assertNull(captor.getValue().getKeyword());
    }

    @Test
    @DisplayName("Test 9: Novel search uses default parameters when not provided")
    void testNovelSearchDefaultParameters() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 0, new ArrayList<>(), 0, 1, 0, 0L
        );
        when(searchService.searchNovels(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act
        mockMvc.perform(get("/api/search/novels")
                        .param("keyword", "test"))
                .andExpect(status().isOk());

        // Assert
        ArgumentCaptor<SearchRequestDTO> captor = ArgumentCaptor.forClass(SearchRequestDTO.class);
        verify(searchService).searchNovels(captor.capture());
        assertEquals(1, captor.getValue().getPage()); // default page
        assertEquals(10, captor.getValue().getPageSize()); // default pageSize
        assertEquals("created_at", captor.getValue().getSortBy()); // default sortBy
        assertEquals("DESC", captor.getValue().getSortOrder()); // default sortOrder
    }

    @Test
    @DisplayName("Test 10: Combined search returns correct JSON structure")
    void testCombinedSearchJsonStructure() throws Exception {
        // Arrange
        SearchResponseDTO mockResponse = new SearchResponseDTO(
                new ArrayList<>(), 2, new ArrayList<>(), 3, 1, 1, 5L
        );
        when(searchService.search(any(SearchRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/search")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.novels").isArray())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.novelCount").value(2))
                .andExpect(jsonPath("$.userCount").value(3))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalResults").value(5));

        verify(searchService, times(1)).search(any(SearchRequestDTO.class));
    }
}
