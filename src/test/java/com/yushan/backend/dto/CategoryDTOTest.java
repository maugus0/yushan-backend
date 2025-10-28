package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category DTO Tests")
class CategoryDTOTest {

    @Test
    @DisplayName("Test CategoryCreateRequestDTO - Getter and Setter")
    void testCategoryCreateRequestDTOGetterSetter() {
        CategoryCreateRequestDTO dto = new CategoryCreateRequestDTO();
        
        // Test name setter and getter
        dto.setName("Fantasy");
        assertEquals("Fantasy", dto.getName());
        
        dto.setName("Science Fiction");
        assertEquals("Science Fiction", dto.getName());
        
        dto.setName("Romance");
        assertEquals("Romance", dto.getName());
        
        // Test description setter and getter
        dto.setDescription("Fantasy novels");
        assertEquals("Fantasy novels", dto.getDescription());
        
        dto.setDescription("Sci-fi novels");
        assertEquals("Sci-fi novels", dto.getDescription());
        
        // Test with null
        dto.setName(null);
        dto.setDescription(null);
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        
        // Test with empty string
        dto.setName("");
        dto.setDescription("");
        assertEquals("", dto.getName());
        assertEquals("", dto.getDescription());
        
        // Test with long strings
        dto.setName("Very Long Category Name That Exceeds Normal Length");
        dto.setDescription("Very Long Description That Exceeds Normal Length And Should Still Work");
        assertEquals("Very Long Category Name That Exceeds Normal Length", dto.getName());
        assertEquals("Very Long Description That Exceeds Normal Length And Should Still Work", dto.getDescription());
    }
    
    @Test
    @DisplayName("Test CategoryCreateRequestDTO - toString")
    void testCategoryCreateRequestDTOToString() {
        CategoryCreateRequestDTO dto = new CategoryCreateRequestDTO();
        dto.setName("Test Category");
        dto.setDescription("Test Description");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CategoryCreateRequestDTO"));
        assertTrue(toString.contains("name"));
        assertTrue(toString.contains("description"));
    }

    @Test
    @DisplayName("Test CategoryUpdateRequestDTO - Getter and Setter")
    void testCategoryUpdateRequestDTOGetterSetter() {
        CategoryUpdateRequestDTO dto = new CategoryUpdateRequestDTO();
        
        // Test name setter and getter
        dto.setName("Science Fiction");
        assertEquals("Science Fiction", dto.getName());
        
        dto.setName("Updated Fiction");
        assertEquals("Updated Fiction", dto.getName());
        
        // Test description setter and getter
        dto.setDescription("Sci-fi novels");
        assertEquals("Sci-fi novels", dto.getDescription());
        
        dto.setDescription("Updated description");
        assertEquals("Updated description", dto.getDescription());
        
        // Test isActive setter and getter
        dto.setIsActive(true);
        assertTrue(dto.getIsActive());
        
        dto.setIsActive(false);
        assertFalse(dto.getIsActive());
        
        dto.setIsActive(null);
        assertNull(dto.getIsActive());
        
        // Test with null values
        dto.setName(null);
        dto.setDescription(null);
        dto.setIsActive(null);
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getIsActive());
        
        // Test with empty strings
        dto.setName("");
        dto.setDescription("");
        assertEquals("", dto.getName());
        assertEquals("", dto.getDescription());
    }
    
    @Test
    @DisplayName("Test CategoryUpdateRequestDTO - toString")
    void testCategoryUpdateRequestDTOToString() {
        CategoryUpdateRequestDTO dto = new CategoryUpdateRequestDTO();
        dto.setName("Updated Category");
        dto.setDescription("Updated Description");
        dto.setIsActive(true);
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CategoryUpdateRequestDTO"));
    }

    @Test
    @DisplayName("Test CategoryResponseDTO")
    void testCategoryResponseDTO() {
        Date now = new Date();
        
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(1);
        dto.setName("Romance");
        dto.setDescription("Romance novels");
        dto.setSlug("romance");
        dto.setIsActive(true);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        
        assertEquals(1, dto.getId());
        assertEquals("Romance", dto.getName());
        assertEquals("Romance novels", dto.getDescription());
        assertEquals("romance", dto.getSlug());
        assertTrue(dto.getIsActive());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date testDate = new Date();
        dto.setCreateTime(testDate);
        Date retrieved = dto.getCreateTime();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        dto.setUpdateTime(testDate);
        retrieved = dto.getUpdateTime();
        assertNotSame(testDate, retrieved);
    }

    @Test
    @DisplayName("Test CategoryResponseDTO constructor")
    void testCategoryResponseDTOConstructor() {
        Date now = new Date();
        CategoryResponseDTO dto = new CategoryResponseDTO(1, "Test", "Desc", "test", true, now, now);
        
        assertEquals(1, dto.getId());
        assertEquals("Test", dto.getName());
        assertEquals("Desc", dto.getDescription());
        assertEquals("test", dto.getSlug());
        assertTrue(dto.getIsActive());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
    }

    @Test
    @DisplayName("Test CategoryListResponseDTO")
    void testCategoryListResponseDTO() {
        CategoryResponseDTO cat1 = new CategoryResponseDTO();
        cat1.setId(1);
        cat1.setName("Fantasy");
        
        CategoryResponseDTO cat2 = new CategoryResponseDTO();
        cat2.setId(2);
        cat2.setName("Sci-Fi");
        
        List<CategoryResponseDTO> categories = new java.util.ArrayList<>(Arrays.asList(cat1, cat2));
        CategoryListResponseDTO dto = new CategoryListResponseDTO(categories, 2);
        
        assertEquals(2, dto.getCategories().size());
        assertEquals(2, dto.getTotalCount());
        
        // Test defensive copying
        categories.add(new CategoryResponseDTO());
        assertEquals(2, dto.getCategories().size());
        
        List<CategoryResponseDTO> retrieved = dto.getCategories();
        assertNotSame(categories, retrieved);
        
        // Test default constructor
        CategoryListResponseDTO dto2 = new CategoryListResponseDTO();
        java.util.List<CategoryResponseDTO> categories2 = new java.util.ArrayList<>(Arrays.asList(cat1, cat2));
        dto2.setCategories(categories2);
        dto2.setTotalCount(2);
        assertEquals(2, dto2.getCategories().size());
        assertEquals(2, dto2.getTotalCount());
        
        // Test null categories
        dto2.setCategories(null);
        assertNull(dto2.getCategories());
        
        // Test with different values
        dto2.setTotalCount(100);
        assertEquals(100, dto2.getTotalCount());
        
        // Test equals, hashCode, and canEqual methods
        CategoryUpdateRequestDTO dto3 = new CategoryUpdateRequestDTO();
        dto3.setName("Test");
        dto3.setDescription("Test Desc");
        
        CategoryUpdateRequestDTO dto4 = new CategoryUpdateRequestDTO();
        dto4.setName("Test");
        dto4.setDescription("Test Desc");
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
        
        // Test CategoryResponseDTO equals, hashCode, canEqual
        Date testDate = new Date();
        CategoryResponseDTO dto5 = new CategoryResponseDTO();
        dto5.setId(1);
        dto5.setName("Category");
        dto5.setCreateTime(testDate);
        
        CategoryResponseDTO dto6 = new CategoryResponseDTO();
        dto6.setId(1);
        dto6.setName("Category");
        dto6.setCreateTime(testDate);
        
        assertEquals(dto5, dto6);
        assertEquals(dto5.hashCode(), dto6.hashCode());
        assertNotEquals(dto5, null);
        assertEquals(dto5, dto5);
        assertTrue(dto5.canEqual(dto6));
        
        // Test CategoryCreateRequestDTO equals, hashCode, canEqual
        CategoryCreateRequestDTO dto7 = new CategoryCreateRequestDTO();
        dto7.setName("New Category");
        dto7.setDescription("Description");
        
        CategoryCreateRequestDTO dto8 = new CategoryCreateRequestDTO();
        dto8.setName("New Category");
        dto8.setDescription("Description");
        
        assertEquals(dto7, dto8);
        assertEquals(dto7.hashCode(), dto8.hashCode());
        assertNotEquals(dto7, null);
        assertEquals(dto7, dto7);
        assertTrue(dto7.canEqual(dto8));
    }
    
    @Test
    @DisplayName("Test CategoryListResponseDTO equals, hashCode, canEqual")
    void testCategoryListResponseDTOEqualsHashCode() {
        CategoryResponseDTO cat1 = new CategoryResponseDTO();
        cat1.setId(1);
        cat1.setName("Category 1");
        
        CategoryResponseDTO cat2 = new CategoryResponseDTO();
        cat2.setId(2);
        cat2.setName("Category 2");
        
        List<CategoryResponseDTO> categories1 = new java.util.ArrayList<>(Arrays.asList(cat1, cat2));
        CategoryListResponseDTO listDto1 = new CategoryListResponseDTO(categories1, 2);
        
        List<CategoryResponseDTO> categories2 = new java.util.ArrayList<>(Arrays.asList(cat1, cat2));
        CategoryListResponseDTO listDto2 = new CategoryListResponseDTO(categories2, 2);
        
        List<CategoryResponseDTO> categories3 = new java.util.ArrayList<>(Arrays.asList(cat1));
        CategoryListResponseDTO listDto3 = new CategoryListResponseDTO(categories3, 1);
        
        assertEquals(listDto1, listDto2);
        assertEquals(listDto1.hashCode(), listDto2.hashCode());
        assertNotEquals(listDto1, listDto3);
        assertNotEquals(listDto1, null);
        assertEquals(listDto1, listDto1);
        assertTrue(listDto1.canEqual(listDto2));
        assertTrue(listDto1.canEqual(listDto3));
        assertFalse(listDto1.canEqual(null));
        
        // Test toString
        String toString = listDto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CategoryListResponseDTO"));
    }
}

