package com.yushan.backend.controller;

import com.yushan.backend.common.Result;
import com.yushan.backend.dto.*;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.NovelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @PostMapping
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ResponseEntity<Result<NovelDetailResponseDTO>> createNovel(@Valid @RequestBody NovelCreateRequestDTO req,
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
        return ResponseEntity.created(URI.create("/api/novels/" + dto.getId())).body(Result.success(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@novelGuard.canEdit(#id, authentication)")
    public Result<NovelDetailResponseDTO> updateNovel(@PathVariable Integer id,
                                                @Valid @RequestBody NovelUpdateRequestDTO req) {
        NovelDetailResponseDTO dto = novelService.updateNovel(id, req);
        return Result.success(dto);
    }

    @GetMapping("/{id}")
    public Result<NovelDetailResponseDTO> getNovel(@PathVariable Integer id) {
        NovelDetailResponseDTO dto = novelService.getNovel(id);
        return Result.success(dto);
    }

    @GetMapping
    public Result<NovelSearchResponseDTO> listNovels(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "author", required = false) String authorId) {
        
        // Create request DTO from query parameters
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(page, size, sort, order, 
                                                              categoryId, status, search, authorId);
        
        NovelSearchResponseDTO response = novelService.listNovelsWithPagination(request);
        return Result.success(response);
    }
}
