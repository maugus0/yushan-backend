package com.yushan.backend.service;

import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.entity.Category;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService
 */
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService();
        
        // Inject mock using reflection
        try {
            java.lang.reflect.Field field = CategoryService.class.getDeclaredField("categoryMapper");
            field.setAccessible(true);
            field.set(categoryService, categoryMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock", e);
        }
    }

    @Nested
    @DisplayName("Get All Categories Tests")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Should return all categories")
        void shouldReturnAllCategories() {
            // Given
            List<Category> expectedCategories = Arrays.asList(
                createCategory(1, "Fiction", "Fiction books", "fiction", true),
                createCategory(2, "Non-Fiction", "Non-fiction books", "non-fiction", false)
            );
            when(categoryMapper.selectAll()).thenReturn(expectedCategories);

            // When
            List<Category> result = categoryService.getAllCategories();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedCategories, result);
            verify(categoryMapper).selectAll();
        }

        @Test
        @DisplayName("Should return empty list when no categories exist")
        void shouldReturnEmptyListWhenNoCategoriesExist() {
            // Given
            when(categoryMapper.selectAll()).thenReturn(Collections.emptyList());

            // When
            List<Category> result = categoryService.getAllCategories();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(categoryMapper).selectAll();
        }
    }

    @Nested
    @DisplayName("Get Active Categories Tests")
    class GetActiveCategoriesTests {

        @Test
        @DisplayName("Should return only active categories")
        void shouldReturnOnlyActiveCategories() {
            // Given
            List<Category> expectedCategories = Arrays.asList(
                createCategory(1, "Fiction", "Fiction books", "fiction", true),
                createCategory(3, "Science", "Science books", "science", true)
            );
            when(categoryMapper.selectActiveCategories()).thenReturn(expectedCategories);

            // When
            List<Category> result = categoryService.getActiveCategories();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.stream().allMatch(Category::getIsActive));
            verify(categoryMapper).selectActiveCategories();
        }
    }

    @Nested
    @DisplayName("Get Category By ID Tests")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should return category when ID exists")
        void shouldReturnCategoryWhenIdExists() {
            // Given
            Integer id = 1;
            Category expectedCategory = createCategory(id, "Fiction", "Fiction books", "fiction", true);
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(expectedCategory);

            // When
            Category result = categoryService.getCategoryById(id);

            // Then
            assertNotNull(result);
            assertEquals(expectedCategory, result);
            verify(categoryMapper).selectByPrimaryKey(id);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.getCategoryById(null)
            );
            assertEquals("Category ID cannot be null", exception.getMessage());
            verifyNoInteractions(categoryMapper);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            Integer id = 999;
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(null);

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(id)
            );
            assertEquals("Category not found with id: " + id, exception.getMessage());
            verify(categoryMapper).selectByPrimaryKey(id);
        }
    }

    @Nested
    @DisplayName("Get Category By Slug Tests")
    class GetCategoryBySlugTests {

        @Test
        @DisplayName("Should return category when slug exists")
        void shouldReturnCategoryWhenSlugExists() {
            // Given
            String slug = "fiction";
            Category expectedCategory = createCategory(1, "Fiction", "Fiction books", slug, true);
            when(categoryMapper.selectBySlug(slug)).thenReturn(expectedCategory);

            // When
            Category result = categoryService.getCategoryBySlug(slug);

            // Then
            assertNotNull(result);
            assertEquals(expectedCategory, result);
            verify(categoryMapper).selectBySlug(slug);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when slug is null")
        void shouldThrowExceptionWhenSlugIsNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.getCategoryBySlug(null)
            );
            assertEquals("Category slug cannot be empty", exception.getMessage());
            verifyNoInteractions(categoryMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when slug is empty")
        void shouldThrowExceptionWhenSlugIsEmpty() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.getCategoryBySlug("   ")
            );
            assertEquals("Category slug cannot be empty", exception.getMessage());
            verifyNoInteractions(categoryMapper);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            String slug = "non-existent";
            when(categoryMapper.selectBySlug(slug)).thenReturn(null);

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryBySlug(slug)
            );
            assertEquals("Category not found with slug: " + slug, exception.getMessage());
            verify(categoryMapper).selectBySlug(slug);
        }
    }

    @Nested
    @DisplayName("Create Category Tests")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category successfully with valid data")
        void shouldCreateCategorySuccessfully() {
            // Given
            String name = "Science Fiction";
            String description = "Sci-fi novels";
            when(categoryMapper.selectAll()).thenReturn(Collections.emptyList());
            when(categoryMapper.selectBySlug("science-fiction")).thenReturn(null);

            // When
            Category result = categoryService.createCategory(name, description);

            // Then
            assertNotNull(result);
            assertEquals("Science Fiction", result.getName());
            assertEquals("Sci-fi novels", result.getDescription());
            assertEquals("science-fiction", result.getSlug());
            assertTrue(result.getIsActive());
            assertNotNull(result.getCreateTime());
            assertNotNull(result.getUpdateTime());

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryMapper).insertSelective(captor.capture());
            Category savedCategory = captor.getValue();
            assertEquals("Science Fiction", savedCategory.getName());
            assertEquals("science-fiction", savedCategory.getSlug());
        }

        @Test
        @DisplayName("Should create category with null description")
        void shouldCreateCategoryWithNullDescription() {
            // Given
            String name = "Fantasy";
            when(categoryMapper.selectAll()).thenReturn(Collections.emptyList());
            when(categoryMapper.selectBySlug("fantasy")).thenReturn(null);

            // When
            Category result = categoryService.createCategory(name, null);

            // Then
            assertNotNull(result);
            assertEquals("Fantasy", result.getName());
            assertNull(result.getDescription());
            assertEquals("fantasy", result.getSlug());
            verify(categoryMapper).insertSelective(any(Category.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.createCategory(null, "Description")
            );
            assertEquals("Category name cannot be empty", exception.getMessage());
            verifyNoInteractions(categoryMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.createCategory("   ", "Description")
            );
            assertEquals("Category name cannot be empty", exception.getMessage());
            verifyNoInteractions(categoryMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when name already exists")
        void shouldThrowExceptionWhenNameExists() {
            // Given
            String name = "Fiction";
            List<Category> existingCategories = Arrays.asList(
                createCategory(1, "Fiction", "Existing fiction", "fiction", true)
            );
            when(categoryMapper.selectAll()).thenReturn(existingCategories);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.createCategory(name, "Description")
            );
            assertEquals("Category with name 'Fiction' already exists", exception.getMessage());
        }

        @Test
        @DisplayName("Should generate unique slug when base slug exists")
        void shouldGenerateUniqueSlugWhenBaseSlugExists() {
            // Given
            String name = "Fiction";
            when(categoryMapper.selectAll()).thenReturn(Collections.emptyList());
            when(categoryMapper.selectBySlug("fiction")).thenReturn(createCategory(1, "Fiction", "", "fiction", true));
            when(categoryMapper.selectBySlug("fiction-1")).thenReturn(null);

            // When
            Category result = categoryService.createCategory(name, "Description");

            // Then
            assertEquals("fiction-1", result.getSlug());
            // Note: selectBySlug may be called multiple times during slug generation
            verify(categoryMapper, atLeast(1)).selectBySlug("fiction");
            verify(categoryMapper, atLeast(1)).selectBySlug("fiction-1");
        }
    }

    @Nested
    @DisplayName("Update Category Tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully() {
            // Given
            Integer id = 1;
            Category existing = createCategory(id, "Fiction", "Old description", "fiction", true);
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(existing);
            when(categoryMapper.selectAll()).thenReturn(Arrays.asList(existing));
            when(categoryMapper.selectBySlug("updated-fiction")).thenReturn(null);

            // When
            Category result = categoryService.updateCategory(id, "Updated Fiction", "New description", false);

            // Then
            assertEquals("Updated Fiction", result.getName());
            assertEquals("New description", result.getDescription());
            assertEquals("updated-fiction", result.getSlug());
            assertFalse(result.getIsActive());
            verify(categoryMapper).updateByPrimaryKeySelective(existing);
        }

        @Test
        @DisplayName("Should not update when no changes provided")
        void shouldNotUpdateWhenNoChanges() {
            // Given
            Integer id = 1;
            Category existing = createCategory(id, "Fiction", "Description", "fiction", true);
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(existing);

            // When
            Category result = categoryService.updateCategory(id, null, null, null);

            // Then
            assertEquals(existing, result);
            verify(categoryMapper, never()).updateByPrimaryKeySelective(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            Integer id = 999;
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(null);

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(id, "New Name", "New Description", true)
            );
            assertEquals("Category not found with id: " + id, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when new name already exists")
        void shouldThrowExceptionWhenNewNameExists() {
            // Given
            Integer id = 1;
            Category existing = createCategory(id, "Fiction", "Description", "fiction", true);
            Category other = createCategory(2, "Science", "Other description", "science", true);
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(existing);
            when(categoryMapper.selectAll()).thenReturn(Arrays.asList(existing, other));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.updateCategory(id, "Science", "New Description", true)
            );
            assertEquals("Category with name 'Science' already exists", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Category Tests")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should soft delete category successfully")
        void shouldSoftDeleteCategorySuccessfully() {
            // Given
            Integer id = 1;
            Category existing = createCategory(id, "Fiction", "Description", "fiction", true);
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(existing);
            when(categoryMapper.updateByPrimaryKeySelective(existing)).thenReturn(1);

            // When
            boolean result = categoryService.deleteCategory(id);

            // Then
            assertTrue(result);
            assertFalse(existing.getIsActive());
            verify(categoryMapper).updateByPrimaryKeySelective(existing);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found")
        void shouldThrowExceptionWhenCategoryNotFoundForDelete() {
            // Given
            Integer id = 999;
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(null);

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(id)
            );
            assertEquals("Category not found with id: " + id, exception.getMessage());
        }

        @Test
        @DisplayName("Should hard delete category successfully")
        void shouldHardDeleteCategorySuccessfully() {
            // Given
            Integer id = 1;
            Category existing = createCategory(id, "Fiction", "Description", "fiction", true);
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(existing);
            when(categoryMapper.deleteByPrimaryKey(id)).thenReturn(1);

            // When
            boolean result = categoryService.hardDeleteCategory(id);

            // Then
            assertTrue(result);
            verify(categoryMapper).deleteByPrimaryKey(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found for hard delete")
        void shouldThrowExceptionWhenCategoryNotFoundForHardDelete() {
            // Given
            Integer id = 999;
            when(categoryMapper.selectByPrimaryKey(id)).thenReturn(null);

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.hardDeleteCategory(id)
            );
            assertEquals("Category not found with id: " + id, exception.getMessage());
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