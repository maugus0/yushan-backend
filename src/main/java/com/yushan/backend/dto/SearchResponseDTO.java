package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified search response DTO for novels and users.
 */
@Data
@NoArgsConstructor
public class SearchResponseDTO {

    // Novel search results
    private List<NovelDetailResponseDTO> novels;
    private Integer novelCount;

    // User search results
    private List<UserProfileResponseDTO> users;
    private Integer userCount;

    // Pagination info
    private Integer currentPage;
    private Integer totalPages;
    private Long totalResults;

    /**
     * Constructor with defensive copying for Lists.
     */
    public SearchResponseDTO(List<NovelDetailResponseDTO> novels, Integer novelCount,
                           List<UserProfileResponseDTO> users, Integer userCount,
                           Integer currentPage, Integer totalPages, Long totalResults) {
        this.novels = novels != null ? new ArrayList<>(novels) : new ArrayList<>();
        this.novelCount = novelCount;
        this.users = users != null ? new ArrayList<>(users) : new ArrayList<>();
        this.userCount = userCount;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    /**
     * Get novels with defensive copy.
     */
    public List<NovelDetailResponseDTO> getNovels() {
        return novels != null ? Collections.unmodifiableList(new ArrayList<>(novels)) : Collections.emptyList();
    }

    /**
     * Set novels with defensive copy.
     */
    public void setNovels(List<NovelDetailResponseDTO> novels) {
        this.novels = novels != null ? new ArrayList<>(novels) : new ArrayList<>();
    }

    /**
     * Get users with defensive copy.
     */
    public List<UserProfileResponseDTO> getUsers() {
        return users != null ? Collections.unmodifiableList(new ArrayList<>(users)) : Collections.emptyList();
    }

    /**
     * Set users with defensive copy.
     */
    public void setUsers(List<UserProfileResponseDTO> users) {
        this.users = users != null ? new ArrayList<>(users) : new ArrayList<>();
    }

    /**
     * Check if there are more pages available.
     */
    public boolean hasNextPage() {
        return currentPage < totalPages;
    }

    /**
     * Check if there is a previous page.
     */
    public boolean hasPreviousPage() {
        return currentPage > 1;
    }

    /**
     * Check if search returned any results.
     */
    public boolean hasResults() {
        return (novels != null && !novels.isEmpty())
                || (users != null && !users.isEmpty());
    }
}
