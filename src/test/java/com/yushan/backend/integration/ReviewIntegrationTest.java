package com.yushan.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.ReviewMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Review;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Review management with real PostgreSQL
 * 
 * This test class verifies:
 * - Review CRUD operations with database persistence
 * - Review permissions and access control
 * - Review search and filtering with database queries
 * - Novel rating updates when reviews are added/updated/deleted
 * - Database transactions and data integrity
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class ReviewIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private User testUser;
    private User authorUser;
    private Novel testNovel;
    private String userToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test data
        createTestData();
    }

    /**
     * Test review creation with database persistence
     */
    @Test
    void testCreateReview_WithDatabasePersistence() throws Exception {
        // Given
        Map<String, Object> reviewRequest = new HashMap<>();
        reviewRequest.put("novelId", testNovel.getId());
        reviewRequest.put("rating", 5);
        reviewRequest.put("title", "Excellent Novel");
        reviewRequest.put("content", "This is an amazing novel with great storytelling.");
        reviewRequest.put("isSpoiler", false);

        // When
        mockMvc.perform(post("/api/reviews")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.title").value("Excellent Novel"))
                .andExpect(jsonPath("$.data.novelId").value(testNovel.getId()));

        // Then - Verify review was persisted in database
        Review createdReview = reviewMapper.selectByUserAndNovel(testUser.getUuid(), testNovel.getId());
        assertThat(createdReview).isNotNull();
        assertThat(createdReview.getRating()).isEqualTo(5);
        assertThat(createdReview.getTitle()).isEqualTo("Excellent Novel");
        assertThat(createdReview.getUserId()).isEqualTo(testUser.getUuid());
        assertThat(createdReview.getNovelId()).isEqualTo(testNovel.getId());
    }

    /**
     * Test review update with database persistence
     */
    @Test
    void testUpdateReview_WithDatabasePersistence() throws Exception {
        // Given - Create review first
        Review existingReview = createTestReview(testUser.getUuid(), testNovel.getId(), 3, "Good Novel", "Decent story");
        reviewMapper.insertSelective(existingReview);
        
        // Get the generated ID from database
        Review insertedReview = reviewMapper.selectByUuid(existingReview.getUuid());
        existingReview.setId(insertedReview.getId());

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("rating", 5);
        updateRequest.put("title", "Updated Excellent Novel");
        updateRequest.put("content", "Updated: This is an amazing novel!");

        // When
        mockMvc.perform(put("/api/reviews/" + existingReview.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.title").value("Updated Excellent Novel"));

        // Then - Verify update was persisted
        Review updatedReview = reviewMapper.selectByPrimaryKey(existingReview.getId());
        assertThat(updatedReview).isNotNull();
        assertThat(updatedReview.getRating()).isEqualTo(5);
        assertThat(updatedReview.getTitle()).isEqualTo("Updated Excellent Novel");
        assertThat(updatedReview.getContent()).isEqualTo("Updated: This is an amazing novel!");
    }

    /**
     * Test review deletion with database removal
     */
    @Test
    void testDeleteReview_WithDatabaseRemoval() throws Exception {
        // Given - Create review first
        Review existingReview = createTestReview(testUser.getUuid(), testNovel.getId(), 4, "Good Novel", "Nice story");
        reviewMapper.insertSelective(existingReview);
        
        // Get the generated ID from database
        Review insertedReview = reviewMapper.selectByUuid(existingReview.getUuid());
        existingReview.setId(insertedReview.getId());

        // When
        mockMvc.perform(delete("/api/reviews/" + existingReview.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("Review deleted successfully"));

        // Then - Verify review was removed from database
        Review deletedReview = reviewMapper.selectByPrimaryKey(existingReview.getId());
        assertThat(deletedReview).isNull();
    }

    /**
     * Test review permissions - user can only update their own reviews
     */
    @Test
    void testReviewPermissions_UserCanOnlyUpdateOwnReviews() throws Exception {
        // Given - Create review by another user
        User anotherUser = createTestUser("another@example.com", "anotheruser");
        userMapper.insert(anotherUser);
        
        Review anotherUserReview = createTestReview(anotherUser.getUuid(), testNovel.getId(), 3, "Another's Review", "Another's content");
        reviewMapper.insertSelective(anotherUserReview);
        
        // Get the generated ID from database
        Review insertedAnotherReview = reviewMapper.selectByUuid(anotherUserReview.getUuid());
        anotherUserReview.setId(insertedAnotherReview.getId());

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("rating", 5);
        updateRequest.put("title", "Unauthorized Update");

        // When - Current user tries to update another user's review
        mockMvc.perform(put("/api/reviews/" + anotherUserReview.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError());

        // Then - Verify review was not updated
        Review unchangedReview = reviewMapper.selectByPrimaryKey(anotherUserReview.getId());
        assertThat(unchangedReview).isNotNull();
        assertThat(unchangedReview.getRating()).isEqualTo(3);
        assertThat(unchangedReview.getTitle()).isEqualTo("Another's Review");
    }


    /**
     * Test duplicate review prevention
     */
    @Test
    void testDuplicateReviewPrevention_WithDatabaseValidation() throws Exception {
        // Given - Create first review
        Review existingReview = createTestReview(testUser.getUuid(), testNovel.getId(), 4, "First Review", "First content");
        reviewMapper.insertSelective(existingReview);
        
        // Get the generated ID from database
        Review insertedReview = reviewMapper.selectByUuid(existingReview.getUuid());
        existingReview.setId(insertedReview.getId());

        Map<String, Object> duplicateRequest = new HashMap<>();
        duplicateRequest.put("novelId", testNovel.getId());
        duplicateRequest.put("rating", 5);
        duplicateRequest.put("title", "Duplicate Review");
        duplicateRequest.put("content", "Duplicate content");

        // When - Try to create duplicate review
        mockMvc.perform(post("/api/reviews")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isInternalServerError());

        // Then - Verify only one review exists
        Review duplicateReview = reviewMapper.selectByUserAndNovel(testUser.getUuid(), testNovel.getId());
        assertThat(duplicateReview).isNotNull();
        assertThat(duplicateReview.getTitle()).isEqualTo("First Review");
    }

    /**
     * Test database transaction rollback on review creation error
     */
    @Test
    void testDatabaseTransactionRollback_OnReviewCreationError() throws Exception {
        // Given - Invalid review data
        Map<String, Object> invalidRequest = new HashMap<>();
        invalidRequest.put("novelId", 99999); // Non-existent novel
        invalidRequest.put("rating", 5);
        invalidRequest.put("title", "Invalid Review");
        invalidRequest.put("content", "Invalid content");

        // When
        mockMvc.perform(post("/api/reviews")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound());

        // Then - Verify no review was created in database
        Review invalidReview = reviewMapper.selectByUserAndNovel(testUser.getUuid(), 99999);
        assertThat(invalidReview).isNull();
    }

    /**
     * Helper method to create test data
     */
    private void createTestData() {
        // Create test user
        testUser = createTestUser("reviewuser@example.com", "reviewuser");
        userMapper.insert(testUser);

        // Create author user
        authorUser = createTestUser("author@example.com", "author");
        authorUser.setIsAuthor(true);
        userMapper.insert(authorUser);

        // Create test novel
        testNovel = createTestNovel("Review Test Novel", "A novel for testing reviews");
        testNovel.setAuthorId(authorUser.getUuid());
        novelMapper.insert(testNovel);

        // Generate token
        userToken = jwtUtil.generateAccessToken(testUser);
    }

    /**
     * Helper method to create test user
     */
    private User createTestUser(String email, String username) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(true);
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setStatus(0); // Active status
        user.setGender(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLogin(new Date());
        user.setLastActive(new Date());
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setLevel(1);
        user.setExp(0.0f);
        user.setYuan(0.0f);
        user.setReadTime(0.0f);
        user.setReadBookNum(0);
        return user;
    }

    /**
     * Helper method to create test novel
     */
    private Novel createTestNovel(String title, String description) {
        Novel novel = new Novel();
        novel.setId(Math.abs(UUID.randomUUID().hashCode()));
        novel.setUuid(UUID.randomUUID());
        novel.setTitle(title);
        novel.setSynopsis(description);
        novel.setCategoryId(1); // Fantasy category
        novel.setStatus(2); // PUBLISHED status
        novel.setVoteCnt(0); // Initialize vote count to 0
        novel.setCreateTime(new Date());
        novel.setUpdateTime(new Date());
        return novel;
    }

    /**
     * Helper method to create test review
     */
    private Review createTestReview(UUID userId, Integer novelId, Integer rating, String title, String content) {
        Review review = new Review();
        review.setUuid(UUID.randomUUID());
        review.setUserId(userId);
        review.setNovelId(novelId);
        review.setRating(rating);
        review.setTitle(title);
        review.setContent(content);
        review.setLikeCnt(0);
        review.setIsSpoiler(false);
        Date now = new Date();
        review.setCreateTime(now);
        review.setUpdateTime(now);
        return review;
    }
}
