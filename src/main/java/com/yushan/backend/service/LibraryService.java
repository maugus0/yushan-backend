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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private NovelLibraryMapper novelLibraryMapper;

    @Autowired
    private LibraryMapper libraryMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    /**
     * add novel to library
     * @param userId
     * @param novelId
     * @return
     */
    public void addNovelToLibrary(UUID userId, Integer novelId, Integer progress) {
        checkValidation(novelId, progress);
        // check if already in library
        if (novelFromLibrary(userId, novelId) != null) {
            throw new ValidationException("novel has existed in library");
        }

        NovelLibrary novelLibrary = new NovelLibrary();
        novelLibrary.setNovelId(novelId);

        Library library = libraryMapper.selectByUserId(userId);
        if (library == null) {
            throw new ResourceNotFoundException("User with ID " + userId + " does not have a library");
        }
        novelLibrary.setLibraryId(library.getId());

        novelLibrary.setProgress(progress);

        novelLibraryMapper.insertSelective(novelLibrary);
    }

    /**
     * remove novel from library
     * @param userId
     * @param novelId
     */
    public void removeNovelFromLibrary(UUID userId, Integer novelId) {
        // check if novel exists
        if (novelMapper.selectByPrimaryKey(novelId) == null) {
            throw new ResourceNotFoundException("novel not found: " + novelId);
        }

        // check if not in library
        NovelLibrary novelLibrary = novelFromLibrary(userId, novelId);
        if (novelLibrary == null) {
            throw new ValidationException("novel don't exist in library");
        }

        novelLibraryMapper.deleteByPrimaryKey(novelLibrary.getId());
    }

    /**
     * batch remove novels from library
     * @param userId
     * @param novelIds
     */
    public void batchRemoveNovelsFromLibrary(UUID userId, List<Integer> novelIds) {
        if (novelIds == null || novelIds.isEmpty()) {
            return;
        }

        List<Novel> foundNovels = novelMapper.selectByIds(novelIds);
        if (foundNovels.size() != novelIds.size()) {
            throw new ResourceNotFoundException("One or more novels not found.");
        }

        List<NovelLibrary> libraryEntries = novelLibraryMapper.selectByUserIdAndNovelIds(userId, novelIds);
        if (libraryEntries.size() != novelIds.size()) {
            throw new ValidationException("One or more novels are not in your library.");
        }

        novelLibraryMapper.deleteByUserIdAndNovelIds(userId, novelIds);
    }

    /**
     * get user's library
     * @param userId
     * @param page
     * @param size
     * @param sort
     * @param order
     * @return
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<LibraryResponseDTO> getUserLibrary(UUID userId, int page, int size, String sort, String order) {
        int offset = page * size;
        long totalElements = novelLibraryMapper.countByUserId(userId);

        if (totalElements == 0) {
            return new PageResponseDTO<>(Collections.emptyList(), 0L, page, size);
        }

        String safeSort = "updateTime".equalsIgnoreCase(sort) ? "update_time" : "create_time";
        String safeOrder = "asc".equalsIgnoreCase(order) ? "ASC" : "DESC";
        List<NovelLibrary> novelLibraries = novelLibraryMapper.selectByUserIdWithPagination(userId, offset, size, safeSort, safeOrder);

        if (novelLibraries.isEmpty()) {
            return new PageResponseDTO<>(Collections.emptyList(), totalElements, page, size);
        }

        List<Integer> novelIds = novelLibraries.stream()
                .map(NovelLibrary::getNovelId)
                .distinct()
                .collect(Collectors.toList());
        List<Integer> chapterIds = novelLibraries.stream()
                .map(NovelLibrary::getProgress)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());


        Map<Integer, Novel> novelMap = novelMapper.selectByIds(novelIds).stream()
                .collect(Collectors.toMap(Novel::getId, novel -> novel));
        Map<Integer, Chapter> chapterMap = chapterMapper.selectByIds(chapterIds).stream()
                .collect(Collectors.toMap(Chapter::getId, chapter -> chapter));

        List<LibraryResponseDTO> dtos = novelLibraries.stream()
                .map(novelLibrary -> {
                    Novel novel = novelMap.get(novelLibrary.getNovelId());
                    Chapter chapter = chapterMap.get(novelLibrary.getProgress());
                    return convertToDTO(novelLibrary, novel, chapter);
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(dtos, totalElements, page, size);
    }

    /**
     * update a novel's reading progress
     * @param userId
     * @param novelId
     * @param progress
     * @return
     */
    public LibraryResponseDTO updateReadingProgress(UUID userId, Integer novelId, Integer progress) {
        checkValidation(novelId, progress);
        // check if not in library
        NovelLibrary novelLibrary = novelFromLibrary(userId, novelId);
        if (novelLibrary == null) {
            throw new ValidationException("novel don't exist in library");
        }

        novelLibrary.setProgress(progress);

        novelLibraryMapper.updateByPrimaryKeySelective(novelLibrary);

        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        Chapter chapter = chapterMapper.selectByPrimaryKey(progress);

        return convertToDTO(novelLibrary, novel, chapter);
    }

    /**
     * get a novel from library for GET API
     * @param userId
     * @param novelId
     * @return LibraryResponseDTO
     */
    public LibraryResponseDTO getNovel(UUID userId, Integer novelId){
        // check if not in library
        NovelLibrary novelLibrary = novelFromLibrary(userId, novelId);
        if (novelLibrary == null) {
            throw new ValidationException("novel don't exist in library");
        }
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        Chapter chapter = chapterMapper.selectByPrimaryKey(novelLibrary.getProgress());
        return convertToDTO(novelLibrary, novel, chapter);
    }

    /**
     * get a novel from library
     * @param userId
     * @param novelId
     * @return NovelLibrary
     */
    public NovelLibrary novelFromLibrary(UUID userId, Integer novelId) {
        return novelLibraryMapper.selectByUserIdAndNovelId(userId, novelId);
    }

    /**
     * check if a novel is in library
     * @param userId
     * @param novelId
     * @return boolean
     */
    public boolean novelInLibrary(UUID userId, Integer novelId) {
        return novelLibraryMapper.selectByUserIdAndNovelId(userId, novelId) != null;
    }

    /**
     * Batch check if a list of novels are in the user's library.
     * @param userId
     * @param novelIds
     * @return A Map where the key is the novelId and the value is true if it's in the library, false otherwise.
     */
    public Map<Integer, Boolean> checkNovelsInLibrary(UUID userId, List<Integer> novelIds) {
        if (novelIds == null || novelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // Find all library entries that match the user and novel IDs
        Set<Integer> novelsInLibrary = novelLibraryMapper.selectByUserIdAndNovelIds(userId, novelIds)
                .stream()
                .map(NovelLibrary::getNovelId)
                .collect(Collectors.toSet());

        // Build the result map
        return novelIds.stream()
                .collect(Collectors.toMap(
                        novelId -> novelId,      // Key is the novelId
                        novelsInLibrary::contains // Value is true if the set contains the novelId
                ));
    }

    private void checkValidation(Integer novelId, Integer progress) {
        // check if novel exists
        if (novelMapper.selectByPrimaryKey(novelId) == null) {
            throw new ResourceNotFoundException("novel not found: " + novelId);
        }
        Chapter chapter = chapterMapper.selectByPrimaryKey(progress);
        if (chapter == null) {
            throw new ResourceNotFoundException("Chapter not found with id: " + progress);
        }
        if (!chapter.getNovelId().equals(novelId)) {
            throw new ValidationException("Chapter don't belong with novel id: " + novelId);
        }
    }

    private LibraryResponseDTO convertToDTO(NovelLibrary novelLibrary, Novel novel, Chapter chapter) {
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setId(novelLibrary.getId());
        dto.setNovelId(novelLibrary.getNovelId());
        dto.setProgress(novelLibrary.getProgress());
        dto.setCreateTime(novelLibrary.getCreateTime());
        dto.setUpdateTime(novelLibrary.getUpdateTime());

        if (novel != null) {
            dto.setNovelTitle(novel.getTitle());
            dto.setNovelAuthor(novel.getAuthorName());
            dto.setNovelCover(novel.getCoverImgUrl());
            dto.setChapterCnt(novel.getChapterCnt());
        }
        if (chapter != null) {
            dto.setChapterNumber(chapter.getChapterNumber());
        }
        return dto;
    }
}
