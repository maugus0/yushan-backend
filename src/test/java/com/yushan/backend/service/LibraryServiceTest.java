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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryServiceTest {

    @Mock
    private NovelMapper novelMapper;

    @Mock
    private LibraryMapper libraryMapper;

    @Mock
    private NovelLibraryMapper novelLibraryMapper;

    @InjectMocks
    private LibraryService libraryService;

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

    @Test
    void addNovelToLibrary_Success() {
        // Given
        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setChapterCnt(10);

        Library library = new Library();
        library.setId(100);

        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
        when(novelLibraryMapper.selectByUserIdAndNovelId(testUserId, novelId)).thenReturn(null);
        when(libraryMapper.selectByUserId(testUserId)).thenReturn(library);

        // When
        libraryService.addNovelToLibrary(testUserId, novelId, 5);

        // Then
        verify(novelLibraryMapper).insertSelective(argThat(novelLibrary ->
                novelLibrary.getNovelId().equals(novelId) &&
                        novelLibrary.getProgress() == 5 &&
                        novelLibrary.getLibraryId().equals(100)));
    }

    @Test
    void addNovelToLibrary_NovelNotFound() {
        // Given
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(null);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.addNovelToLibrary(testUserId, novelId, 5)
        );

        assertTrue(exception.getMessage().contains("novel not found: " + novelId));
    }

    @Test
    void removeNovelFromLibrary_Success() {
        // Given
        Novel novel = new Novel();
        novel.setId(novelId);

        NovelLibrary novelLibrary = new NovelLibrary();
        novelLibrary.setId(1);
        novelLibrary.setNovelId(novelId);
        novelLibrary.setLibraryId(100); // 假设关联的libraryId为100

        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
        when(novelLibraryMapper.selectByUserIdAndNovelId(testUserId, novelId)).thenReturn(novelLibrary);

        // When
        libraryService.removeNovelFromLibrary(testUserId, novelId);

        // Then
        verify(novelLibraryMapper).deleteByPrimaryKey(1);
    }

    @Test
    void batchRemoveNovelsFromLibrary_Success() {
        // Given
        Novel novel1 = new Novel();
        novel1.setId(1);
        Novel novel2 = new Novel();
        novel2.setId(2);
        Novel novel3 = new Novel();
        novel3.setId(3);

        NovelLibrary library1 = new NovelLibrary();
        library1.setId(1);
        library1.setNovelId(1);
        library1.setLibraryId(100);

        NovelLibrary library2 = new NovelLibrary();
        library2.setId(2);
        library2.setNovelId(2);
        library2.setLibraryId(100);

        NovelLibrary library3 = new NovelLibrary();
        library3.setId(3);
        library3.setNovelId(3);
        library3.setLibraryId(100);

        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel1);
        when(novelMapper.selectByPrimaryKey(2)).thenReturn(novel2);
        when(novelMapper.selectByPrimaryKey(3)).thenReturn(novel3);

        when(novelLibraryMapper.selectByUserIdAndNovelId(testUserId, 1)).thenReturn(library1);
        when(novelLibraryMapper.selectByUserIdAndNovelId(testUserId, 2)).thenReturn(library2);
        when(novelLibraryMapper.selectByUserIdAndNovelId(testUserId, 3)).thenReturn(library3);

        // When
        libraryService.batchRemoveNovelsFromLibrary(testUserId, novelIds);

        // Then
        verify(novelLibraryMapper).deleteByUserIdAndNovelIds(testUserId, novelIds);
    }

    @Test
    void getUserLibrary_Success() {
        // Given
        NovelLibrary library1 = new NovelLibrary();
        library1.setId(1);
        library1.setNovelId(1);
        library1.setProgress(5);
        library1.setLibraryId(100);
        library1.setCreateTime(new Date());
        library1.setUpdateTime(new Date());

        NovelLibrary library2 = new NovelLibrary();
        library2.setId(2);
        library2.setNovelId(2);
        library2.setProgress(10);
        library2.setLibraryId(100);
        library2.setCreateTime(new Date());
        library2.setUpdateTime(new Date());

        List<NovelLibrary> libraries = Arrays.asList(library1, library2);

        Novel novel1 = new Novel();
        novel1.setId(1);
        novel1.setTitle("Novel 1");
        novel1.setAuthorName("Author 1");
        novel1.setCoverImgUrl("cover1.jpg");

        Novel novel2 = new Novel();
        novel2.setId(2);
        novel2.setTitle("Novel 2");
        novel2.setAuthorName("Author 2");
        novel2.setCoverImgUrl("cover2.jpg");

        when(novelLibraryMapper.countByUserId(testUserId)).thenReturn(2L);
        when(novelLibraryMapper.selectByUserIdWithPagination(testUserId, 0, 10, "create_time", "DESC"))
                .thenReturn(libraries);
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel1);
        when(novelMapper.selectByPrimaryKey(2)).thenReturn(novel2);

        // When
        PageResponseDTO<LibraryResponseDTO> result = libraryService.getUserLibrary(testUserId, 0, 10, "createTime", "desc");

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void updateReadingProgress_Success() {
        // Given
        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setChapterCnt(20);

        NovelLibrary novelLibrary = new NovelLibrary();
        novelLibrary.setId(1);
        novelLibrary.setNovelId(novelId);
        novelLibrary.setProgress(5);
        novelLibrary.setLibraryId(100);

        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);
        when(novelLibraryMapper.selectByUserIdAndNovelId(testUserId, novelId)).thenReturn(novelLibrary);
        when(novelMapper.selectByPrimaryKey(novelId)).thenReturn(novel);

        // When
        LibraryResponseDTO result = libraryService.updateReadingProgress(testUserId, novelId, 10);

        // Then
        assertNotNull(result);
        verify(novelLibraryMapper).updateByPrimaryKeySelective(any(NovelLibrary.class));
    }
}