package com.yushan.backend.service;

import com.yushan.backend.dao.SearchMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SearchMapper searchMapper;

    /**
     * Combined search for novels and users.
     */
    public SearchResponseDTO search(SearchRequestDTO request) {
        // Calculate offset for pagination
        Integer offset = (request.getPage() - 1) * request.getPageSize();

        // Search novels
        List<Novel> novels = searchMapper.searchNovels(
                request.getKeyword(),
                request.getCategory(),
                offset,
                request.getPageSize(),
                request.getSortBy(),
                request.getSortOrder()
        );
        Integer novelCount = searchMapper.countNovels(
                request.getKeyword(),
                request.getCategory()
        );

        // Search users (only if no category filter, since users don't have categories)
        List<User> users = List.of();
        Integer userCount = 0;
        if (request.getCategory() == null || request.getCategory().isEmpty()) {
            users = searchMapper.searchUsers(
                    request.getKeyword(),
                    offset,
                    request.getPageSize(),
                    request.getSortBy(),
                    request.getSortOrder()
            );
            userCount = searchMapper.countUsers(request.getKeyword());
        }

        // Convert to DTOs
        List<NovelDetailResponseDTO> novelDTOs = novels.stream()
                .map(this::convertToNovelDTO)
                .collect(Collectors.toList());

        List<UserProfileResponseDTO> userDTOs = users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        // Calculate pagination
        Long totalResults = novelCount.longValue() + userCount.longValue();
        Integer totalPages = (int) Math.ceil((double) totalResults / request.getPageSize());

        // Build response
        SearchResponseDTO response = new SearchResponseDTO();
        response.setNovels(novelDTOs);
        response.setUsers(userDTOs);
        response.setNovelCount(novelCount);
        response.setUserCount(userCount);
        response.setCurrentPage(request.getPage());
        response.setTotalPages(totalPages);
        response.setTotalResults(totalResults);

        return response;
    }

    /**
     * Search only novels.
     */
    public SearchResponseDTO searchNovels(SearchRequestDTO request) {
        Integer offset = (request.getPage() - 1) * request.getPageSize();

        List<Novel> novels = searchMapper.searchNovels(
                request.getKeyword(),
                request.getCategory(),
                offset,
                request.getPageSize(),
                request.getSortBy(),
                request.getSortOrder()
        );
        Integer novelCount = searchMapper.countNovels(
                request.getKeyword(),
                request.getCategory()
        );

        List<NovelDetailResponseDTO> novelDTOs = novels.stream()
                .map(this::convertToNovelDTO)
                .collect(Collectors.toList());

        Integer totalPages = (int) Math.ceil((double) novelCount / request.getPageSize());

        SearchResponseDTO response = new SearchResponseDTO();
        response.setNovels(novelDTOs);
        response.setUsers(List.of());
        response.setNovelCount(novelCount);
        response.setUserCount(0);
        response.setCurrentPage(request.getPage());
        response.setTotalPages(totalPages);
        response.setTotalResults(novelCount.longValue());

        return response;
    }

    /**
     * Search only users.
     */
    public SearchResponseDTO searchUsers(SearchRequestDTO request) {
        Integer offset = (request.getPage() - 1) * request.getPageSize();

        List<User> users = searchMapper.searchUsers(
                request.getKeyword(),
                offset,
                request.getPageSize(),
                request.getSortBy(),
                request.getSortOrder()
        );
        Integer userCount = searchMapper.countUsers(request.getKeyword());

        List<UserProfileResponseDTO> userDTOs = users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        Integer totalPages = (int) Math.ceil((double) userCount / request.getPageSize());

        SearchResponseDTO response = new SearchResponseDTO();
        response.setNovels(List.of());
        response.setUsers(userDTOs);
        response.setNovelCount(0);
        response.setUserCount(userCount);
        response.setCurrentPage(request.getPage());
        response.setTotalPages(totalPages);
        response.setTotalResults(userCount.longValue());

        return response;
    }

    // Helper methods to convert entities to DTOs
    private NovelDetailResponseDTO convertToNovelDTO(Novel novel) {
        NovelDetailResponseDTO dto = new NovelDetailResponseDTO();
        BeanUtils.copyProperties(novel, dto);
        return dto;
    }

    private UserProfileResponseDTO convertToUserDTO(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        BeanUtils.copyProperties(user, dto);
        // Explicitly convert UUID to String
        if (user.getUuid() != null) {
            dto.setUuid(user.getUuid().toString());
        }
        return dto;
    }
}