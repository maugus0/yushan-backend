package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    /**
     *
     * @param page
     * @param size
     * @param sortType: view/vote
     * @param categoryId: can be null
     * @param timeRange: weekly, monthly, overall
     * @return
     */
    @GetMapping("/novel")
    public ApiResponse<PageResponseDTO<NovelDetailResponseDTO>> getNovelRanking (
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "50") Integer size,
            @RequestParam(value = "sortType", defaultValue = "view") String sortType,
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "timeRange", defaultValue = "overall") String timeRange) {
        PageResponseDTO<NovelDetailResponseDTO> response = rankingService.rankNovel(page, size, sortType, categoryId, timeRange);
        return ApiResponse.success("Novels retrieved successfully", response);
    }

    /**
     *
     * @param page
     * @param size
     * @param timeRange
     * @return
     */
    @GetMapping("/user")
    public ApiResponse<PageResponseDTO<UserProfileResponseDTO>> getUserRanking (
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "50") Integer size,
            @RequestParam(value = "timeRange", defaultValue = "overall") String timeRange) {
        PageResponseDTO<UserProfileResponseDTO> response = rankingService.rankUser(page,size, timeRange);
        return ApiResponse.success("Novels retrieved successfully", response);
    }

    /**
     *
     * @param page
     * @param size
     * @param sortType: novelNum/view/vote
     * @param timeRange
     * @return
     */
    @GetMapping("/author")
    public ApiResponse<PageResponseDTO<AuthorResponseDTO>> getAuthorRanking (
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "50") Integer size,
            @RequestParam(value = "sortType", defaultValue = "vote") String sortType,
            @RequestParam(value = "timeRange", defaultValue = "overall") String timeRange) {
        PageResponseDTO<AuthorResponseDTO> response = rankingService.rankAuthor(page,size, sortType, timeRange);
        return ApiResponse.success("Novels retrieved successfully", response);
    }

    /**
     * Get ranking information for a specific novel
     * @param novelId The ID of the novel
     * @param sortType The sorting type (view/vote)
     * @param categoryId Optional category filter
     * @return NovelRankingResponseDTO with ranking information
     */
    @GetMapping("/novel/{novelId}")
    public ApiResponse<NovelRankingResponseDTO> getNovelRanking(
            @PathVariable("novelId") Integer novelId,
            @RequestParam(value = "sortType", defaultValue = "view") String sortType,
            @RequestParam(value = "category", required = false) Integer categoryId) {
        NovelRankingResponseDTO response = rankingService.getNovelRanking(novelId, sortType, categoryId);
        return ApiResponse.success("Novel ranking retrieved successfully", response);
    }
}