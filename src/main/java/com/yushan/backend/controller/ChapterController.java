package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.ChapterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chapters")
@CrossOrigin(origins = "*")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    /**
     * Create a new chapter
     * Author only - must be the author of the novel
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChapterDetailResponseDTO> createChapter(
            @Valid @RequestBody ChapterCreateRequestDTO requestDTO,
            Authentication authentication) {
        UUID userId = extractUserId(authentication);
        ChapterDetailResponseDTO chapter = chapterService.createChapter(userId, requestDTO);
        return ApiResponse.success("Chapter created successfully", chapter);
    }

    /**
     * Batch create chapters
     * Author only - must be the author of the novel
     */
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> batchCreateChapters(
            @Valid @RequestBody ChapterBatchCreateRequestDTO requestDTO,
            Authentication authentication) {
        UUID userId = extractUserId(authentication);
        chapterService.batchCreateChapters(userId, requestDTO);
        return ApiResponse.success("Chapters created successfully");
    }

    /**
     * Get chapter by UUID (public endpoint)
     * Returns full chapter content
     */
    @GetMapping("/{uuid}")
    public ApiResponse<ChapterDetailResponseDTO> getChapterByUuid(@PathVariable UUID uuid) {
        ChapterDetailResponseDTO chapter = chapterService.getChapterByUuid(uuid);
        return ApiResponse.success("Chapter retrieved successfully", chapter);
    }

    /**
     * Get chapter by novel ID and chapter number (public endpoint)
     * Alternative way to access chapters using chapter number
     */
    @GetMapping("/novel/{novelId}/number/{chapterNumber}")
    public ApiResponse<ChapterDetailResponseDTO> getChapterByNovelIdAndNumber(
            @PathVariable Integer novelId,
            @PathVariable Integer chapterNumber) {
        ChapterDetailResponseDTO chapter = chapterService.getChapterByNovelIdAndNumber(novelId, chapterNumber);
        return ApiResponse.success("Chapter retrieved successfully", chapter);
    }

    /**
     * Get all chapters for a novel with pagination (public endpoint)
     * Use publishedOnly=true for public view, false for author dashboard
     */
    @GetMapping("/novel/{novelId}")
    public ApiResponse<ChapterListResponseDTO> getChaptersByNovelId(
            @PathVariable Integer novelId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "publishedOnly", defaultValue = "true") Boolean publishedOnly) {
        ChapterListResponseDTO chapters = chapterService.getChaptersByNovelId(novelId, page, pageSize, publishedOnly);
        return ApiResponse.success("Chapters retrieved successfully", chapters);
    }

    /**
     * Search chapters with filters (public endpoint)
     * Advanced search with multiple criteria
     */
    @PostMapping("/search")
    public ApiResponse<ChapterListResponseDTO> searchChapters(
            @Valid @RequestBody ChapterSearchRequestDTO requestDTO) {
        ChapterListResponseDTO chapters = chapterService.searchChapters(requestDTO);
        return ApiResponse.success("Chapters retrieved successfully", chapters);
    }

    /**
     * Get chapter statistics for a novel
     * Author only - for dashboard analytics
     */
    @GetMapping("/novel/{novelId}/statistics")
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ApiResponse<ChapterStatisticsResponseDTO> getChapterStatistics(
            @PathVariable Integer novelId,
            Authentication authentication) {
        ChapterStatisticsResponseDTO statistics = chapterService.getChapterStatistics(novelId);
        return ApiResponse.success("Statistics retrieved successfully", statistics);
    }

    /**
     * Update chapter
     * Author only - must be the author of the novel
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ApiResponse<ChapterDetailResponseDTO> updateChapter(
            @Valid @RequestBody ChapterUpdateRequestDTO requestDTO,
            Authentication authentication) {
        UUID userId = extractUserId(authentication);
        ChapterDetailResponseDTO chapter = chapterService.updateChapter(userId, requestDTO);
        return ApiResponse.success("Chapter updated successfully", chapter);
    }

    /**
     * Publish/unpublish a chapter or schedule it
     * Author only - must be the author of the novel
     */
    @PatchMapping("/publish")
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ApiResponse<String> publishChapter(
            @Valid @RequestBody ChapterPublishRequestDTO requestDTO,
            Authentication authentication) {
        UUID userId = extractUserId(authentication);
        chapterService.publishChapter(userId, requestDTO);
        return ApiResponse.success("Chapter publish status updated successfully");
    }

    /**
     * Batch publish/unpublish all chapters of a novel
     * Author only - must be the author of the novel
     */
    @PatchMapping("/novel/{novelId}/publish")
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ApiResponse<String> batchPublishChapters(
            @PathVariable Integer novelId,
            @RequestParam Boolean isValid,
            Authentication authentication) {
        UUID userId = extractUserId(authentication);
        chapterService.batchPublishChapters(userId, novelId, isValid);
        return ApiResponse.success("Chapters publish status updated successfully");
    }

    /**
     * Increment view count for a chapter (public endpoint)
     * Called when a user reads a chapter
     */
    @PostMapping("/{uuid}/view")
    public ApiResponse<String> incrementViewCount(@PathVariable UUID uuid) {
        chapterService.incrementViewCount(uuid);
        return ApiResponse.success("View count incremented");
    }

    /**
     * Delete a chapter (soft delete)
     * Author only - must be the author of the novel
     */
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ApiResponse<String> deleteChapter(
            @PathVariable UUID uuid,
            Authentication authentication) {
        UUID userId = extractUserId(authentication);
        chapterService.deleteChapter(userId, uuid);
        return ApiResponse.success("Chapter deleted successfully");
    }

    /**
     * Delete all chapters of a novel (soft delete)
     * Author only - must be the author of the novel
     * Use with caution!
     */
    @DeleteMapping("/novel/{novelId}")
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ApiResponse<String> deleteChaptersByNovelId(
            @PathVariable Integer novelId,
            Authentication authentication) {
        UUID userId = extractUserId(authentication);
        chapterService.deleteChaptersByNovelId(userId, novelId);
        return ApiResponse.success("All chapters deleted successfully");
    }

    /**
     * Get next chapter UUID for navigation (public endpoint)
     * Used for "Next Chapter" button
     */
    @GetMapping("/{uuid}/next")
    public ApiResponse<UUID> getNextChapter(@PathVariable UUID uuid) {
        UUID nextUuid = chapterService.getNextChapterUuid(uuid);
        return ApiResponse.success("Next chapter retrieved", nextUuid);
    }

    /**
     * Get previous chapter UUID for navigation (public endpoint)
     * Used for "Previous Chapter" button
     */
    @GetMapping("/{uuid}/previous")
    public ApiResponse<UUID> getPreviousChapter(@PathVariable UUID uuid) {
        UUID prevUuid = chapterService.getPreviousChapterUuid(uuid);
        return ApiResponse.success("Previous chapter retrieved", prevUuid);
    }

    /**
     * Check if chapter exists (public endpoint)
     * Useful for validation before creating chapters
     */
    @GetMapping("/exists")
    public ApiResponse<Boolean> chapterExists(
            @RequestParam Integer novelId,
            @RequestParam Integer chapterNumber) {
        boolean exists = chapterService.chapterExists(novelId, chapterNumber);
        return ApiResponse.success("Chapter existence checked", exists);
    }

    /**
     * Get next available chapter number for a novel
     * Author only - useful when creating new chapters
     */
    @GetMapping("/novel/{novelId}/next-number")
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ApiResponse<Integer> getNextAvailableChapterNumber(
            @PathVariable Integer novelId,
            Authentication authentication) {
        Integer nextNumber = chapterService.getNextAvailableChapterNumber(novelId);
        return ApiResponse.success("Next chapter number retrieved", nextNumber);
    }

    /**
     * Helper method to extract user ID from authentication
     */
    private UUID extractUserId(Authentication authentication) {
        Object principal = authentication != null ? authentication.getPrincipal() : null;
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails cud = (CustomUserDetails) principal;
            return cud.getUserId() != null ? UUID.fromString(cud.getUserId()) : null;
        }
        return null;
    }
}
