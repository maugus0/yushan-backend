package com.yushan.backend.service;

import com.yushan.backend.dao.LibraryMapper;
import com.yushan.backend.dao.NovelLibraryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.LibraryResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.entity.Library;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.NovelLibrary;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private NovelMapper novelMapper;
    @Mock
    private NovelLibraryMapper novelLibraryMapper;
    @Mock
    private LibraryMapper libraryMapper;

    @InjectMocks
    private LibraryService libraryService;

    private UUID userId;
    private Integer novelId;
    private Library userLibrary;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        novelId = 1;
        userLibrary = new Library();
        userLibrary.setId(100);
        userLibrary.setUserId(userId);
    }

    @Nested
    @DisplayName("addNovelToLibrary Tests")
    class AddNovelToLibrary {
        @Test
        @DisplayName("Should add a novel to library successfully")
        void shouldAddNovelSuccessfully() {
            // Given
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(new Novel());
            when(novelLibraryMapper.selectByUserIdAndNovelId(userId, novelId)).thenReturn(null);
            when(libraryMapper.selectByUserId(userId)).thenReturn(userLibrary);
            when(novelMapper.selectByPrimaryKey(anyInt())).thenReturn(createNovel(novelId, "Test Novel"));

            // When
            libraryService.addNovelToLibrary(userId, novelId, 1);

            // Then
            verify(novelLibraryMapper).insertSelective(argThat(nl ->
                    nl.getNovelId().equals(novelId) &&
                            nl.getLibraryId().equals(userLibrary.getId()) &&
                            nl.getProgress().equals(1)
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if novel does not exist")
        void shouldThrowException_whenNovelNotFound() {
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(null);
            assertThrows(ResourceNotFoundException.class, () -> libraryService.addNovelToLibrary(userId, novelId, 1));
        }

        @Test
        @DisplayName("Should throw ValidationException if novel is already in library")
        void shouldThrowException_whenNovelAlreadyInLibrary() {
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(new Novel());
            when(novelLibraryMapper.selectByUserIdAndNovelId(userId, novelId)).thenReturn(new NovelLibrary());
            assertThrows(ValidationException.class, () -> libraryService.addNovelToLibrary(userId, novelId, 1));
        }
    }

    @Nested
    @DisplayName("getUserLibrary Tests")
    class GetUserLibrary {
        @Test
        @DisplayName("Should use batch operations to get library and avoid N+1 problem")
        void shouldUseBatchOperations() {
            // Given
            NovelLibrary nl1 = createNovelLibrary(1, 1);
            NovelLibrary nl2 = createNovelLibrary(2, 2);
            List<NovelLibrary> novelLibraries = Arrays.asList(nl1, nl2);
            Novel novel1 = createNovel(1, "Novel One");
            Novel novel2 = createNovel(2, "Novel Two");

            when(novelLibraryMapper.countByUserId(userId)).thenReturn(2L);
            when(novelLibraryMapper.selectByUserIdWithPagination(userId, 0, 10, "create_time", "DESC")).thenReturn(novelLibraries);
            when(novelMapper.selectByIds(anyList())).thenReturn(Arrays.asList(novel1, novel2));

            // When
            PageResponseDTO<LibraryResponseDTO> result = libraryService.getUserLibrary(userId, 0, 10, "createTime", "desc");

            // Then
            assertEquals(2, result.getContent().size());
            assertEquals("Novel One", result.getContent().get(0).getNovelTitle());

            // Crucial Verifications
            verify(novelMapper, times(1)).selectByIds(anyList());
            verify(novelMapper, never()).selectByPrimaryKey(anyInt());
        }

        @Test
        @DisplayName("Should return an empty page when library is empty")
        void shouldReturnEmptyPage_whenLibraryIsEmpty() {
            when(novelLibraryMapper.countByUserId(userId)).thenReturn(0L);

            PageResponseDTO<LibraryResponseDTO> result = libraryService.getUserLibrary(userId, 0, 10, "createTime", "desc");

            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
            verify(novelLibraryMapper, never()).selectByUserIdWithPagination(any(), anyInt(), anyInt(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("checkNovelsInLibrary Tests")
    class CheckNovelsInLibrary {
        @Test
        @DisplayName("Should return a map indicating which novels are in the library")
        void shouldReturnCorrectMap() {
            // Given
            List<Integer> novelIdsToCheck = Arrays.asList(1, 2, 3); // Check for novels 1, 2, 3
            NovelLibrary nl1 = createNovelLibrary(1, 1);
            NovelLibrary nl3 = createNovelLibrary(3, 3);
            when(novelLibraryMapper.selectByUserIdAndNovelIds(userId, novelIdsToCheck)).thenReturn(Arrays.asList(nl1, nl3));

            // When
            Map<Integer, Boolean> result = libraryService.checkNovelsInLibrary(userId, novelIdsToCheck);

            // Then
            assertEquals(3, result.size());
            assertTrue(result.get(1));
            assertFalse(result.get(2));
            assertTrue(result.get(3));
        }

        @Test
        @DisplayName("Should return an empty map when given an empty list of novel IDs")
        void shouldReturnEmptyMapForEmptyList() {
            Map<Integer, Boolean> result = libraryService.checkNovelsInLibrary(userId, Collections.emptyList());
            assertTrue(result.isEmpty());
            verify(novelLibraryMapper, never()).selectByUserIdAndNovelIds(any(), any());
        }
    }


    // Helper methods
    private NovelLibrary createNovelLibrary(Integer id, Integer novelId) {
        return new NovelLibrary(id, 100, novelId, 1, new Date(), new Date());
    }
    private Novel createNovel(Integer id, String title) {
        Novel n = new Novel();
        n.setId(id);
        n.setTitle(title);
        n.setAuthorName("Author");
        n.setCoverImgUrl("cover.jpg");
        return n;
    }
}