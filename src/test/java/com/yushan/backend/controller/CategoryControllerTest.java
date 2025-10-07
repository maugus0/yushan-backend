package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Category;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CategoryController REST endpoints
 */
class CategoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("GET /api/categories - Get All Categories")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Should return all categories successfully")
        void shouldReturnAllCategoriesSuccessfully() throws Exception {
            // Given
            List<Category> categories = Arrays.asList(
                createCategory(1, "Fiction", "Fiction books", "fiction", true),
                createCategory(2, "Non-Fiction", "Non-fiction books", "non-fiction", false)
            );
            when(categoryService.getAllCategories()).thenReturn(categories);

            // When & Then
            mockMvc.perform(get("/api/categories"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Categories retrieved successfully"))
                    .andExpect(jsonPath("$.data.categories").isArray())
                    .andExpect(jsonPath("$.data.categories.length()").value(2))
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.categories[0].id").value(1))
                    .andExpect(jsonPath("$.data.categories[0].name").value("Fiction"))
                    .andExpect(jsonPath("$.data.categories[0].slug").value("fiction"))
                    .andExpect(jsonPath("$.data.categories[1].id").value(2))
                    .andExpect(jsonPath("$.data.categories[1].name").value("Non-Fiction"))
                    .andExpect(jsonPath("$.data.categories[1].isActive").value(false));

            verify(categoryService).getAllCategories();
        }

        @Test
        @DisplayName("Should return empty list when no categories exist")
        void shouldReturnEmptyListWhenNoCategoriesExist() throws Exception {
            // Given
            when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.categories").isArray())
                    .andExpect(jsonPath("$.data.categories.length()").value(0))
                    .andExpect(jsonPath("$.data.totalCount").value(0));

            verify(categoryService).getAllCategories();
        }
    }

    @Nested
    @DisplayName("GET /api/categories/active - Get Active Categories")
    class GetActiveCategoriesTests {

        @Test
        @DisplayName("Should return only active categories")
        void shouldReturnOnlyActiveCategories() throws Exception {
            // Given
            List<Category> activeCategories = Arrays.asList(
                createCategory(1, "Fiction", "Fiction books", "fiction", true),
                createCategory(3, "Science", "Science books", "science", true)
            );
            when(categoryService.getActiveCategories()).thenReturn(activeCategories);

            // When & Then
            mockMvc.perform(get("/api/categories/active"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Active categories retrieved successfully"))
                    .andExpect(jsonPath("$.data.categories").isArray())
                    .andExpect(jsonPath("$.data.categories.length()").value(2))
                    .andExpect(jsonPath("$.data.categories[0].isActive").value(true))
                    .andExpect(jsonPath("$.data.categories[1].isActive").value(true));

            verify(categoryService).getActiveCategories();
        }
    }

    @Nested
    @DisplayName("GET /api/categories/{id} - Get Category By ID")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should return category when ID exists")
        void shouldReturnCategoryWhenIdExists() throws Exception {
            // Given
            Integer id = 1;
            Category category = createCategory(id, "Fiction", "Fiction books", "fiction", true);
            when(categoryService.getCategoryById(id)).thenReturn(category);

            // When & Then
            mockMvc.perform(get("/api/categories/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Category retrieved successfully"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Fiction"))
                    .andExpect(jsonPath("$.data.slug").value("fiction"))
                    .andExpect(jsonPath("$.data.isActive").value(true));

            verify(categoryService).getCategoryById(id);
        }

        @Test
        @DisplayName("Should return 404 when category not found")
        void shouldReturn404WhenCategoryNotFound() throws Exception {
            // Given
            Integer id = 999;
            when(categoryService.getCategoryById(id))
                    .thenThrow(new ResourceNotFoundException("Category not found with id: " + id));

            // When & Then
            mockMvc.perform(get("/api/categories/{id}", id))
                    .andExpect(status().isNotFound());

            verify(categoryService).getCategoryById(id);
        }
    }

    @Nested
    @DisplayName("GET /api/categories/slug/{slug} - Get Category By Slug")
    class GetCategoryBySlugTests {

        @Test
        @DisplayName("Should return category when slug exists")
        void shouldReturnCategoryWhenSlugExists() throws Exception {
            // Given
            String slug = "fiction";
            Category category = createCategory(1, "Fiction", "Fiction books", slug, true);
            when(categoryService.getCategoryBySlug(slug)).thenReturn(category);

            // When & Then
            mockMvc.perform(get("/api/categories/slug/{slug}", slug))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.slug").value("fiction"))
                    .andExpect(jsonPath("$.data.name").value("Fiction"));

            verify(categoryService).getCategoryBySlug(slug);
        }

        @Test
        @DisplayName("Should return 404 when category not found by slug")
        void shouldReturn404WhenCategoryNotFoundBySlug() throws Exception {
            // Given
            String slug = "non-existent";
            when(categoryService.getCategoryBySlug(slug))
                    .thenThrow(new ResourceNotFoundException("Category not found with slug: " + slug));

            // When & Then
            mockMvc.perform(get("/api/categories/slug/{slug}", slug))
                    .andExpect(status().isNotFound());

            verify(categoryService).getCategoryBySlug(slug);
        }
    }

    @Nested
    @DisplayName("POST /api/categories - Create Category")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategorySuccessfully() throws Exception {
            // Given
            CategoryCreateRequestDTO request = new CategoryCreateRequestDTO();
            request.setName("Science Fiction");
            request.setDescription("Sci-fi novels");

            Category createdCategory = createCategory(1, "Science Fiction", "Sci-fi novels", "science-fiction", true);
            when(categoryService.createCategory("Science Fiction", "Sci-fi novels"))
                    .thenReturn(createdCategory);

            // When & Then
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Category created successfully"))
                    .andExpect(jsonPath("$.data.name").value("Science Fiction"))
                    .andExpect(jsonPath("$.data.slug").value("science-fiction"))
                    .andExpect(jsonPath("$.data.isActive").value(true));

            verify(categoryService).createCategory("Science Fiction", "Sci-fi novels");
        }

        @Test
        @DisplayName("Should return 400 when request body is invalid")
        void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
            // Given
            CategoryCreateRequestDTO request = new CategoryCreateRequestDTO();
            // name is null - should fail validation

            // When & Then
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(categoryService);
        }

        @Test
        @DisplayName("Should create category successfully with null description")
        void shouldCreateCategorySuccessfullyWithNullDescription() throws Exception {
            // Given
            CategoryCreateRequestDTO request = new CategoryCreateRequestDTO();
            request.setName("Fantasy");
            // description is null

            Category createdCategory = createCategory(1, "Fantasy", null, "fantasy", true);
            when(categoryService.createCategory("Fantasy", null))
                    .thenReturn(createdCategory);

            // When & Then
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("Fantasy"))
                    .andExpect(jsonPath("$.data.description").isEmpty());

            verify(categoryService).createCategory("Fantasy", null);
        }
    }

    @Nested
    @DisplayName("PUT /api/categories/{id} - Update Category")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully() throws Exception {
            // Given
            Integer id = 1;
            CategoryUpdateRequestDTO request = new CategoryUpdateRequestDTO();
            request.setName("Updated Fiction");
            request.setDescription("Updated description");
            request.setIsActive(false);

            Category updatedCategory = createCategory(id, "Updated Fiction", "Updated description", "updated-fiction", false);
            when(categoryService.updateCategory(id, "Updated Fiction", "Updated description", false))
                    .thenReturn(updatedCategory);

            // When & Then
            mockMvc.perform(put("/api/categories/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("Updated Fiction"))
                    .andExpect(jsonPath("$.data.isActive").value(false));

            verify(categoryService).updateCategory(id, "Updated Fiction", "Updated description", false);
        }

        @Test
        @DisplayName("Should return 404 when category not found for update")
        void shouldReturn404WhenCategoryNotFoundForUpdate() throws Exception {
            // Given
            Integer id = 999;
            CategoryUpdateRequestDTO request = new CategoryUpdateRequestDTO();
            request.setName("Updated Fiction");

            when(categoryService.updateCategory(eq(id), anyString(), any(), any()))
                    .thenThrow(new ResourceNotFoundException("Category not found with id: " + id));

            // When & Then
            mockMvc.perform(put("/api/categories/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(categoryService).updateCategory(eq(id), anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/categories/{id} - Soft Delete Category")
    class SoftDeleteCategoryTests {

        @Test
        @DisplayName("Should soft delete category successfully")
        void shouldSoftDeleteCategorySuccessfully() throws Exception {
            // Given
            Integer id = 1;
            when(categoryService.deleteCategory(id)).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/categories/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Category deactivated successfully"));

            verify(categoryService).deleteCategory(id);
        }

        @Test
        @DisplayName("Should return 404 when category not found for delete")
        void shouldReturn404WhenCategoryNotFoundForDelete() throws Exception {
            // Given
            Integer id = 999;
            when(categoryService.deleteCategory(id))
                    .thenThrow(new ResourceNotFoundException("Category not found with id: " + id));

            // When & Then
            mockMvc.perform(delete("/api/categories/{id}", id))
                    .andExpect(status().isNotFound());

            verify(categoryService).deleteCategory(id);
        }
    }

    @Nested
    @DisplayName("DELETE /api/categories/{id}/hard - Hard Delete Category")
    class HardDeleteCategoryTests {

        @Test
        @DisplayName("Should hard delete category successfully")
        void shouldHardDeleteCategorySuccessfully() throws Exception {
            // Given
            Integer id = 1;
            when(categoryService.hardDeleteCategory(id)).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/categories/{id}/hard", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Category permanently deleted"));

            verify(categoryService).hardDeleteCategory(id);
        }

        @Test
        @DisplayName("Should return 404 when category not found for hard delete")
        void shouldReturn404WhenCategoryNotFoundForHardDelete() throws Exception {
            // Given
            Integer id = 999;
            when(categoryService.hardDeleteCategory(id))
                    .thenThrow(new ResourceNotFoundException("Category not found with id: " + id));

            // When & Then
            mockMvc.perform(delete("/api/categories/{id}/hard", id))
                    .andExpect(status().isNotFound());

            verify(categoryService).hardDeleteCategory(id);
        }
    }

    /**
     * Helper method to create a Category for testing
     */
    private Category createCategory(Integer id, String name, String description, String slug, Boolean isActive) {
        Date now = new Date();
        return new Category(id, name, description, slug, isActive, now, now);
    }
}