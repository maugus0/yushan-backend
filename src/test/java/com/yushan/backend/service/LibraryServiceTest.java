package com.yushan.backend.service;

import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.LibraryMapper;
import com.yushan.backend.dao.NovelLibraryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.LibraryResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.entity.Chapter;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private NovelMapper novelMapper;
    @Mock
    private NovelLibraryMapper novelLibraryMapper;
    @Mock
    private LibraryMapper libraryMapper;
    @Mock
    private ChapterMapper chapterMapper;

    @InjectMocks
    private LibraryService libraryService;

    private UUID userId;
    private Integer novelId;
    private Integer chapterId;
    private Novel testNovel;
    private Chapter testChapter;
    private Library testLibrary;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        novelId = 128; // Use a value > 127 to test Integer object comparison
        chapterId = 500; // Use a value > 127

        testNovel = new Novel();
        testNovel.setId(novelId);

        testChapter = new Chapter();
        testChapter.setId(chapterId);
        testChapter.setNovelId(novelId); // Ensure chapter belongs to the novel

        testLibrary = new Library();
        testLibrary.setId(1);
        testLibrary.setUserId(userId);
    }

    @Nested
    @DisplayName("addNovelToLibrary Tests")
    class AddNovelToLibrary {

        @Test
        @DisplayName("Should add novel successfully when library exists")
        void shouldAddNovelSuccessfully() {
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(testNovel);
            when(chapterMapper.selectByPrimaryKey(chapterId)).thenReturn(testChapter);
            when(novelLibraryMapper.selectByUserIdAndNovelId(userId, novelId)).thenReturn(null);
            when(libraryMapper.selectByUserId(userId)).thenReturn(testLibrary);

            libraryService.addNovelToLibrary(userId, novelId, chapterId);

            verify(novelLibraryMapper).insertSelective(argThat(nl ->
                    nl.getNovelId().equals(novelId) &&
                            nl.getProgress().equals(chapterId) &&
                            nl.getLibraryId().equals(testLibrary.getId())
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if user's library does not exist")
        void shouldThrowWhenLibraryNotFound() {
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(testNovel);
            when(chapterMapper.selectByPrimaryKey(chapterId)).thenReturn(testChapter);
            when(novelLibraryMapper.selectByUserIdAndNovelId(userId, novelId)).thenReturn(null);
            when(libraryMapper.selectByUserId(userId)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () ->
                    libraryService.addNovelToLibrary(userId, novelId, chapterId));
        }

        @Test
        @DisplayName("Should throw ValidationException if novel already in library")
        void shouldThrowWhenNovelExists() {
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(testNovel);
            when(chapterMapper.selectByPrimaryKey(chapterId)).thenReturn(testChapter);
            when(novelLibraryMapper.selectByUserIdAndNovelId(userId, novelId)).thenReturn(new NovelLibrary());

            assertThrows(ValidationException.class, () ->
                    libraryService.addNovelToLibrary(userId, novelId, chapterId));
        }

        @Test
        @DisplayName("Should throw ValidationException if chapter does not belong to the novel")
        void shouldThrowWhenChapterDoesNotBelongToNovel() {
            testChapter.setNovelId(999); // Set a different novelId
            when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(testNovel);
            when(chapterMapper.selectByPrimaryKey(chapterId)).thenReturn(testChapter);

            assertThrows(ValidationException.class, () ->
                    libraryService.addNovelToLibrary(userId, novelId, chapterId));
        }
    }

    @Nested
    @DisplayName("batchRemoveNovelsFromLibrary Tests")
    class BatchRemoveNovels {

        @Test
        @DisplayName("Should perform batch remove successfully with correct validations")
        void shouldBatchRemoveSuccessfully() {
            List<Integer> novelIds = Arrays.asList(1, 2);
            when(novelMapper.selectByIds(novelIds)).thenReturn(Arrays.asList(new Novel(), new Novel()));
            when(novelLibraryMapper.selectByUserIdAndNovelIds(userId, novelIds)).thenReturn(Arrays.asList(new NovelLibrary(), new NovelLibrary()));

            libraryService.batchRemoveNovelsFromLibrary(userId, novelIds);

            verify(novelLibraryMapper).deleteByUserIdAndNovelIds(userId, novelIds);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if one of the novels does not exist")
        void shouldThrowWhenOneNovelNotFound() {
            List<Integer> novelIds = Arrays.asList(1, 999); // 999 does not exist
            when(novelMapper.selectByIds(novelIds)).thenReturn(Collections.singletonList(new Novel())); // Only returns one novel

            assertThrows(ResourceNotFoundException.class, () ->
                    libraryService.batchRemoveNovelsFromLibrary(userId, novelIds));
        }

        @Test
        @DisplayName("Should throw ValidationException if one of the novels is not in the library")
        void shouldThrowWhenOneNovelNotInLibrary() {
            List<Integer> novelIds = Arrays.asList(1, 2);
            when(novelMapper.selectByIds(novelIds)).thenReturn(Arrays.asList(new Novel(), new Novel()));
            when(novelLibraryMapper.selectByUserIdAndNovelIds(userId, novelIds)).thenReturn(Collections.singletonList(new NovelLibrary())); // Only returns one entry

            assertThrows(ValidationException.class, () ->
                    libraryService.batchRemoveNovelsFromLibrary(userId, novelIds));
        }

        @Test
        @DisplayName("Should do nothing if list of novelIds is empty or null")
        void shouldDoNothingForEmptyList() {
            libraryService.batchRemoveNovelsFromLibrary(userId, Collections.emptyList());
            libraryService.batchRemoveNovelsFromLibrary(userId, null);

            verifyNoInteractions(novelMapper);
            verifyNoInteractions(novelLibraryMapper);
        }
    }

    @Nested
    @DisplayName("getUserLibrary (Batch Optimized) Tests")
    class GetUserLibrary {
        @Test
        @DisplayName("Should use batch operations to avoid N+1 problem")
        void shouldAvoidNPlusOne() {
            List<NovelLibrary> novelLibraries = Arrays.asList(createNovelLibrary(1, 1), createNovelLibrary(2, 2));
            when(novelLibraryMapper.countByUserId(userId)).thenReturn(2L);
            when(novelLibraryMapper.selectByUserIdWithPagination(any(), anyInt(), anyInt(), any(), any())).thenReturn(novelLibraries);
            when(novelMapper.selectByIds(anyList())).thenReturn(Arrays.asList(createNovel(1, "N1"), createNovel(2, "N2")));
            when(chapterMapper.selectByIds(anyList())).thenReturn(Collections.emptyList());

            PageResponseDTO<LibraryResponseDTO> result = libraryService.getUserLibrary(userId, 0, 10, "createTime", "desc");

            assertEquals(2, result.getContent().size());
            verify(novelMapper, times(1)).selectByIds(anyList());
            verify(chapterMapper, times(1)).selectByIds(anyList());
            verify(novelMapper, never()).selectByPrimaryKey(anyInt());
            verify(chapterMapper, never()).selectByPrimaryKey(anyInt());
        }

        @Test
        @DisplayName("Should return empty page if no items in library")
        void shouldReturnEmptyPage() {
            when(novelLibraryMapper.countByUserId(userId)).thenReturn(0L);

            PageResponseDTO<LibraryResponseDTO> result = libraryService.getUserLibrary(userId, 0, 10, "createTime", "desc");

            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
            verify(novelLibraryMapper, never()).selectByUserIdWithPagination(any(), anyInt(), anyInt(), any(), any());
        }
    }

    // Helper methods for creating mock entities
    private NovelLibrary createNovelLibrary(Integer id, Integer novelId) {
        return new NovelLibrary(id, 100, novelId, 1, new Date(), new Date());
    }
    private Novel createNovel(Integer id, String title) {
        Novel n = new Novel(); n.setId(id); n.setTitle(title); return n;
    }
}