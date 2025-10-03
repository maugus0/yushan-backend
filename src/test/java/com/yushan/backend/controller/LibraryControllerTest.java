package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.entity.NovelLibrary;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.exception.UnauthorizedException;
import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.security.CustomUserDetailsService;
import com.yushan.backend.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryControllerTest {

    @Mock
    private LibraryService libraryService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetailsService.CustomUserDetails userDetails;

    @InjectMocks
    private LibraryController libraryController;

    private UUID testUserId;
    private Integer novelId;
    private List<Integer> novelIds;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUserId = UUID.randomUUID();
        novelId = 1;
        novelIds = Arrays.asList(1, 2, 3);
    }

    private void mockAuthentication() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());
    }

    @Test
    void addNovelToLibrary_Success() {
        // Given
        mockAuthentication();
        LibraryRequestDTO request = new LibraryRequestDTO();
        request.setProgress(5);

        // When
        ApiResponse<String> response = libraryController.addNovelToLibrary(novelId, request, authentication);

        // Then
        assertNotNull(response);
        assertEquals(response.getCode(), ErrorCode.SUCCESS.getCode());
        assertEquals("Add novel to library successfully", response.getMessage());

        verify(libraryService).addNovelToLibrary(testUserId, novelId, 5);
    }

    @Test
    void removeNovelFromLibrary_Success() {
        // Given
        mockAuthentication();

        // When
        ApiResponse<String> response = libraryController.removeNovelFromLibrary(novelId, authentication);

        // Then
        assertNotNull(response);
        assertEquals(response.getCode(), ErrorCode.SUCCESS.getCode());
        assertEquals("Remove novel from library successfully", response.getMessage());

        verify(libraryService).removeNovelFromLibrary(testUserId, novelId);
    }

    @Test
    void batchRemoveNovelsFromLibrary_Success() {
        // Given
        mockAuthentication();

        BatchRequestDTO batchRequestDTO = new BatchRequestDTO();
        batchRequestDTO.setIds(novelIds);

        // When
        ApiResponse<String> response = libraryController.batchRemoveNovelsFromLibrary(batchRequestDTO, authentication);

        // Then
        assertNotNull(response);
        assertEquals(response.getCode(), ErrorCode.SUCCESS.getCode());
        assertEquals("batch remove successfully", response.getMessage());

        verify(libraryService).batchRemoveNovelsFromLibrary(testUserId, novelIds);
    }


    @Test
    void getUserLibrary_Success() {
        // Given
        mockAuthentication();
        PageResponseDTO<LibraryResponseDTO> pageResponse = new PageResponseDTO<>();
        when(libraryService.getUserLibrary(testUserId, 0, 10, "createTime", "desc"))
                .thenReturn(pageResponse);

        // When
        ApiResponse<PageResponseDTO<LibraryResponseDTO>> response = libraryController.getUserLibrary(
                0, 10, "createTime", "desc", authentication);

        // Then
        assertNotNull(response);
        assertEquals(response.getCode(), ErrorCode.SUCCESS.getCode());
        assertEquals("Novels retrieved successfully", response.getMessage());
        assertSame(pageResponse, response.getData());
    }

    @Test
    void checkNovelInLibrary_Success() {
        // Given
        mockAuthentication();
        when(libraryService.novelInLibrary(testUserId, novelId)).thenReturn(true);

        // When
        ApiResponse<Boolean> response = libraryController.checkNovelInLibrary(novelId, authentication);

        // Then
        assertNotNull(response);
        assertEquals(response.getCode(), ErrorCode.SUCCESS.getCode());
        assertTrue(response.getData());
    }

    @Test
    void getLibraryNovelProgress_Success() {
        // Given
        mockAuthentication();
        NovelLibrary novelLibrary = new NovelLibrary();
        novelLibrary.setProgress(5);
        when(libraryService.novelFromLibrary(testUserId, novelId)).thenReturn(novelLibrary);

        // When
        ApiResponse<LibraryResponseDTO> response = libraryController.getLibraryNovel(novelId, authentication);

        // Then
        assertNotNull(response);
        assertEquals(response.getCode(), ErrorCode.SUCCESS.getCode());
        assertEquals(5, response.getData().getProgress());
    }

    @Test
    void getLibraryNovelProgress_NovelNotFound() {
        // Given
        mockAuthentication();
        when(libraryService.novelFromLibrary(testUserId, novelId)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            libraryController.getLibraryNovel(novelId, authentication);
        });
    }

    @Test
    void updateReadingProgress_Success() {
        // Given
        mockAuthentication();
        LibraryRequestDTO request = new LibraryRequestDTO();
        request.setProgress(10);
        LibraryResponseDTO responseDTO = new LibraryResponseDTO();
        when(libraryService.updateReadingProgress(testUserId, novelId, 10)).thenReturn(responseDTO);

        // When
        ApiResponse<LibraryResponseDTO> response = libraryController.updateReadingProgress(
                novelId, request, authentication);

        // Then
        assertNotNull(response);
        assertEquals(response.getCode(), ErrorCode.SUCCESS.getCode());
        assertSame(responseDTO, response.getData());

        verify(libraryService).updateReadingProgress(testUserId, novelId, 10);
    }

    @Test
    void getCurrentUserId_Unauthorized_WhenAuthenticationIsNull() {
        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            libraryController.getCurrentUserId(null);
        });
    }

    @Test
    void getCurrentUserId_Unauthorized_WhenNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            libraryController.getCurrentUserId(authentication);
        });
    }

    @Test
    void getCurrentUserId_ValidationException_WhenUserIdIsNull() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(null);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            libraryController.getCurrentUserId(authentication);
        });
    }

    @Test
    void getCurrentUserId_Unauthorized_WhenInvalidPrincipal() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            libraryController.getCurrentUserId(authentication);
        });
    }

    @Test
    void getCurrentUserId_Success() {
        // Given
        mockAuthentication();

        // When
        UUID result = libraryController.getCurrentUserId(authentication);

        // Then
        assertEquals(testUserId, result);
    }
}
