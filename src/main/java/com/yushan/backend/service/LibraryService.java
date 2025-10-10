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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private NovelLibraryMapper novelLibraryMapper;
    @Autowired
    private LibraryMapper libraryMapper;

    /**
     * add novel to library
     * @param userId
     * @param novelId
     * @return
     */
    public void addNovelToLibrary(UUID userId, Integer novelId, Integer progress) {
        // check if novel exists
        if (novelMapper.selectByPrimaryKey(novelId) == null) {
            throw new ResourceNotFoundException("novel not found: " + novelId);
        }

        // check if already in library
        if (novelFromLibrary(userId, novelId) != null) {
            throw new ValidationException("novel has existed in library");
        }

        NovelLibrary novelLibrary = new NovelLibrary();
        novelLibrary.setNovelId(novelId);

        Library library = libraryMapper.selectByUserId(userId);
        if (library == null) {
            // Create library for user if not exists
            library = new Library();
            library.setUuid(UUID.randomUUID());
            library.setUserId(userId);
            libraryMapper.insertSelective(library);
        }
        novelLibrary.setLibraryId(library.getId());

        //check if progress is valid
        checkProgreess(novelId, progress);
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
        // check if novels exist & in library
        for (Integer novelId : novelIds) {
            // check if novel exists
            if (novelMapper.selectByPrimaryKey(novelId) == null) {
                throw new ResourceNotFoundException("novel not found: " + novelId);
            }

            // check if in library
            NovelLibrary novelLibrary = novelFromLibrary(userId, novelId);
            if (novelLibrary == null) {
                throw new ValidationException("novel don't exist in library");
            }
        }

        // batch remove
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

        String safeSort = "updateTime".equalsIgnoreCase(sort) ? "update_time" : "create_time";
        String safeOrder = "asc".equalsIgnoreCase(order) ? "ASC" : "DESC";

        List<NovelLibrary> novelLibraries = novelLibraryMapper.selectByUserIdWithPagination(
                userId, offset, size, safeSort, safeOrder);

        // convert to DTO
        List<LibraryResponseDTO> dtos = novelLibraries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponseDTO<>(dtos, totalElements, totalPages, page, size,
                page == 0, page >= totalPages - 1, page < totalPages - 1, page > 0);
    }

    /**
     * update a novel's reading progress
     * @param userId
     * @param novelId
     * @param progress
     * @return
     */
    public LibraryResponseDTO updateReadingProgress(UUID userId, Integer novelId, Integer progress) {
        // check if novel exists
        if (novelMapper.selectByPrimaryKey(novelId) == null) {
            throw new ResourceNotFoundException("novel not found");
        }

        // check if not in library
        NovelLibrary novelLibrary = novelFromLibrary(userId, novelId);
        if (novelLibrary == null) {
            throw new ValidationException("novel don't exist in library");
        }

        //check if progress is valid
        checkProgreess(novelId, progress);
        novelLibrary.setProgress(progress);

        novelLibraryMapper.updateByPrimaryKeySelective(novelLibrary);
        return convertToDTO(novelLibrary);
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
        return convertToDTO(novelLibrary);
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
     * check if progress is valid
     * @param novelId
     * @param progress
     */
    private void checkProgreess(Integer novelId, Integer progress) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel != null && novel.getChapterCnt() != null && progress > novel.getChapterCnt()) {
            throw new ValidationException("progress cannot bigger than totalChapterNum");
        }
        if (progress < 1) {
            throw new ValidationException("progress must be greater than or equal to 1");
        }
    }

    protected LibraryResponseDTO convertToDTO(NovelLibrary novelLibrary) {
        LibraryResponseDTO dto = new LibraryResponseDTO();

        dto.setId(novelLibrary.getId());
        dto.setNovelId(novelLibrary.getNovelId());
        dto.setProgress(novelLibrary.getProgress());
        dto.setCreateTime(novelLibrary.getCreateTime());
        dto.setUpdateTime(novelLibrary.getUpdateTime());

        Novel novel = novelMapper.selectByPrimaryKey(novelLibrary.getNovelId());
        if (novel != null) {
            // set novel info
            dto.setNovelTitle(novel.getTitle());
            dto.setNovelAuthor(novel.getAuthorName());
            dto.setNovelCover(novel.getCoverImgUrl());
        }
        return dto;
    }
}
