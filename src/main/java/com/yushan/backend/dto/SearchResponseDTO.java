package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Unified search response DTO for novels and users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
