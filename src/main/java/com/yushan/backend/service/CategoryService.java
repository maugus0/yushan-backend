package com.yushan.backend.service;

import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.entity.Category;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * Get all categories (including inactive ones)
     */
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    /**
     * Get only active categories
     */
    public List<Category> getActiveCategories() {
        return categoryMapper.selectActiveCategories();
    }

    /**
     * Get category by ID
     * Throws ResourceNotFoundException if not found
     */
    public Category getCategoryById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        return category;
    }

    /**
     * Get category by slug
     * Throws ResourceNotFoundException if not found
     */
    public Category getCategoryBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("Category slug cannot be empty");
        }
        Category category = categoryMapper.selectBySlug(slug);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with slug: " + slug);
        }
        return category;
    }

    /**
     * Create a new category
     * Automatically generates slug from name
     */
    @Transactional
    public Category createCategory(String name, String description) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        String trimmedName = name.trim();

        // Check if name already exists (case-insensitive)
        if (categoryNameExists(trimmedName)) {
            throw new IllegalArgumentException("Category with name '" + trimmedName + "' already exists");
        }

        // Generate slug from name
        String slug = generateSlug(trimmedName);

        // Ensure slug uniqueness
        if (categorySlugExists(slug)) {
            slug = generateUniqueSlug(slug);
        }

        // Create category
        Category category = new Category();
        category.setName(trimmedName);
        category.setDescription(description != null ? description.trim() : null);
        category.setSlug(slug);
        category.setIsActive(true);
        Date now = new Date();
        category.setCreateTime(now);
        category.setUpdateTime(now);

        categoryMapper.insertSelective(category);
        return category;
    }

    /**
     * Update an existing category
     * Only updates fields that are provided (not null)
     */
    @Transactional
    public Category updateCategory(Integer id, String name, String description, Boolean isActive) {
        Category existing = categoryMapper.selectByPrimaryKey(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        boolean hasChanges = false;

        // Update name if provided
        if (name != null && !name.trim().isEmpty()) {
            String trimmedName = name.trim();
            if (!trimmedName.equals(existing.getName())) {
                // Check if new name already exists
                if (categoryNameExists(trimmedName)) {
                    throw new IllegalArgumentException("Category with name '" + trimmedName + "' already exists");
                }
                existing.setName(trimmedName);

                // Regenerate slug based on new name
                String newSlug = generateSlug(trimmedName);
                if (categorySlugExists(newSlug) && !newSlug.equals(existing.getSlug())) {
                    newSlug = generateUniqueSlug(newSlug);
                }
                existing.setSlug(newSlug);
                hasChanges = true;
            }
        }

        // Update description if provided
        if (description != null) {
            String trimmedDescription = description.trim();
            if (!trimmedDescription.equals(existing.getDescription())) {
                existing.setDescription(trimmedDescription);
                hasChanges = true;
            }
        }

        // Update active status if provided
        if (isActive != null && !isActive.equals(existing.getIsActive())) {
            existing.setIsActive(isActive);
            hasChanges = true;
        }

        // Only update if there are changes
        if (hasChanges) {
            existing.setUpdateTime(new Date());
            categoryMapper.updateByPrimaryKeySelective(existing);
        }

        return existing;
    }

    /**
     * Soft delete - set isActive to false
     */
    @Transactional
    public boolean deleteCategory(Integer id) {
        Category existing = categoryMapper.selectByPrimaryKey(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        existing.setIsActive(false);
        existing.setUpdateTime(new Date());
        int result = categoryMapper.updateByPrimaryKeySelective(existing);
        return result > 0;
    }

    /**
     * Hard delete - permanently remove from database
     * Use with caution!
     */
    @Transactional
    public boolean hardDeleteCategory(Integer id) {
        Category existing = categoryMapper.selectByPrimaryKey(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        int result = categoryMapper.deleteByPrimaryKey(id);
        return result > 0;
    }

    /**
     * Check if category name already exists (case-insensitive)
     */
    private boolean categoryNameExists(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        List<Category> allCategories = categoryMapper.selectAll();
        return allCategories.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name.trim()));
    }

    /**
     * Check if slug already exists
     */
    private boolean categorySlugExists(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }
        try {
            Category category = categoryMapper.selectBySlug(slug);
            return category != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate URL-friendly slug from input string
     * Example: "Science Fiction" -> "science-fiction"
     */
    private String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Normalize string (remove accents)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalized).replaceAll("");

        // Convert to lowercase and replace spaces/special chars with hyphens
        String slug = withoutAccents.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9]+", "-")  // Replace non-alphanumeric with hyphen
                .replaceAll("^-+", "")          // Remove leading hyphens
                .replaceAll("-+$", "")          // Remove trailing hyphens
                .replaceAll("-+", "-");         // Replace multiple hyphens with single

        return slug;
    }

    /**
     * Generate unique slug by appending counter
     * Example: "science-fiction" -> "science-fiction-1"
     */
    private String generateUniqueSlug(String baseSlug) {
        String uniqueSlug = baseSlug;
        int counter = 1;

        while (categorySlugExists(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }

        return uniqueSlug;
    }
}
