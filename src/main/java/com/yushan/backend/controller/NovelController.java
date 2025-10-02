package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.NovelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @PostMapping
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<NovelDetailResponseDTO> createNovel(@Valid @RequestBody NovelCreateRequestDTO req,
                                              Authentication authentication) {
        Object principal = authentication != null ? authentication.getPrincipal() : null;
        UUID userId = null;
        String authorName = null;
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails cud = (CustomUserDetails) principal;
            userId = cud.getUserId() != null ? UUID.fromString(cud.getUserId()) : null;
            authorName = cud.getProfileUsername();
        } else if (authentication != null) {
            authorName = authentication.getName();
        }

        NovelDetailResponseDTO dto = novelService.createNovel(userId, authorName, req);
        return ApiResponse.success("Novel created successfully", dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@novelGuard.canEdit(#id, authentication)")
    public ApiResponse<NovelDetailResponseDTO> updateNovel(@PathVariable Integer id,
                                             @Valid @RequestBody NovelUpdateRequestDTO req) {
        NovelDetailResponseDTO dto = novelService.updateNovel(id, req);
        return ApiResponse.success("Novel updated successfully", dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<NovelDetailResponseDTO> getNovel(@PathVariable Integer id) {
        NovelDetailResponseDTO dto = novelService.getNovel(id);
        return ApiResponse.success("Novel retrieved successfully", dto);
    }

    @GetMapping
    public ApiResponse<NovelSearchResponseDTO> listNovels(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "authorName", required = false) String authorName) {
        
        // Create request DTO from query parameters
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(page, size, sort, order, 
                                                              categoryId, status, search, authorName);
        
        NovelSearchResponseDTO response = novelService.listNovelsWithPagination(request);
        return ApiResponse.success("Novels retrieved successfully", response);
    }
}
