package com.yushan.backend.controller;

import com.yushan.backend.dto.SearchRequestDTO;
import com.yushan.backend.dto.SearchResponseDTO;
import com.yushan.backend.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * Combined search endpoint - searches both novels and users
     * GET /api/search?keyword=example&category=Fantasy&page=1&pageSize=10
     */
    @GetMapping
    public ResponseEntity<SearchResponseDTO> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder) {

        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword(keyword);
        request.setCategory(category);
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);

        SearchResponseDTO response = searchService.search(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Search only novels
     * GET /api/search/novels?keyword=example&category=Fantasy
     */
    @GetMapping("/novels")
    public ResponseEntity<SearchResponseDTO> searchNovels(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder) {

        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword(keyword);
        request.setCategory(category);
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);

        SearchResponseDTO response = searchService.searchNovels(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Search only users
     * GET /api/search/users?keyword=example
     */
    @GetMapping("/users")
    public ResponseEntity<SearchResponseDTO> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder) {

        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword(keyword);
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);

        SearchResponseDTO response = searchService.searchUsers(request);
        return ResponseEntity.ok(response);
    }
}
