package com.yushan.backend.controller;

import com.yushan.backend.common.Result;
import com.yushan.backend.dto.NovelCreateRequestDTO;
import com.yushan.backend.dto.NovelResponseDTO;
import com.yushan.backend.dto.NovelUpdateRequestDTO;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.NovelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @PostMapping
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN')")
    public ResponseEntity<Result<NovelResponseDTO>> createNovel(@Valid @RequestBody NovelCreateRequestDTO req,
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

        NovelResponseDTO dto = novelService.createNovel(userId, authorName, req);
        return ResponseEntity.created(URI.create("/api/novels/" + dto.getId())).body(Result.success(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@novelGuard.canEdit(#id, authentication)")
    public Result<NovelResponseDTO> updateNovel(@PathVariable Integer id,
                                                @Valid @RequestBody NovelUpdateRequestDTO req) {
        NovelResponseDTO dto = novelService.updateNovel(id, req);
        return Result.success(dto);
    }

    @GetMapping("/{id}")
    public Result<NovelResponseDTO> getNovel(@PathVariable Integer id) {
        NovelResponseDTO dto = novelService.getNovel(id);
        return Result.success(dto);
    }

    @GetMapping
    public Result<List<NovelResponseDTO>> listNovels() {
        // Placeholder for future implementation (filters/pagination)
        return Result.success(Collections.emptyList());
    }
}
