package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Category;
import com.yushan.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Get all categories (public endpoint)
     * Returns all categories including inactive ones
     */
    @GetMapping
    public ApiResponse<CategoryListResponseDTO> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        CategoryListResponseDTO response = CategoryListResponseDTO.fromEntities(categories);
        return ApiResponse.success("Categories retrieved successfully", response);
    }

    /**
     * Get only active categories (public endpoint)
     * Most commonly used endpoint for frontend dropdowns
     */
    @GetMapping("/active")
    public ApiResponse<CategoryListResponseDTO> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        CategoryListResponseDTO response = CategoryListResponseDTO.fromActiveEntities(categories);
        return ApiResponse.success("Active categories retrieved successfully", response);
    }

    /**
     * Get category by ID (public endpoint)
     */
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> getCategoryById(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        CategoryResponseDTO response = CategoryResponseDTO.fromEntity(category);
        return ApiResponse.success("Category retrieved successfully", response);
    }

    /**
     * Get category by slug (public endpoint)
     * Useful for SEO-friendly URLs like /categories/science-fiction
     */
    @GetMapping("/slug/{slug}")
    public ApiResponse<CategoryResponseDTO> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.getCategoryBySlug(slug);
        CategoryResponseDTO response = CategoryResponseDTO.fromEntity(category);
        return ApiResponse.success("Category retrieved successfully", response);
    }

    /**
     * Create a new category (admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryCreateRequestDTO request) {
        Category category = categoryService.createCategory(
                request.getName(),
                request.getDescription()
        );
        CategoryResponseDTO response = CategoryResponseDTO.fromEntity(category);
        return ApiResponse.success("Category created successfully", response);
    }

    /**
     * Update an existing category (admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponseDTO> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryUpdateRequestDTO request) {
        Category category = categoryService.updateCategory(
                id,
                request.getName(),
                request.getDescription(),
                request.getIsActive()
        );
        CategoryResponseDTO response = CategoryResponseDTO.fromEntity(category);
        return ApiResponse.success("Category updated successfully", response);
    }

    /**
     * Soft delete a category (admin only)
     * Sets isActive to false instead of removing from database
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCategory(@PathVariable Integer id) {
        boolean deleted = categoryService.deleteCategory(id);
        if (deleted) {
            return ApiResponse.success("Category deactivated successfully");
        }
        return ApiResponse.error(400, "Failed to deactivate category");
    }

    /**
     * Hard delete a category (admin only)
     * Permanently removes from database - use with caution!
     */
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> hardDeleteCategory(@PathVariable Integer id) {
        boolean deleted = categoryService.hardDeleteCategory(id);
        if (deleted) {
            return ApiResponse.success("Category permanently deleted");
        }
        return ApiResponse.error(400, "Failed to delete category");
    }
}