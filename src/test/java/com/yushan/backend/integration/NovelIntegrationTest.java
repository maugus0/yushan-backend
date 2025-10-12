package com.yushan.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.NovelSearchRequestDTO;
import com.yushan.backend.entity.Novel;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Novel management with real PostgreSQL
 * 
 * This test class verifies:
 * - Novel CRUD operations with database persistence
 * - Author permissions and access control
 * - Novel search and filtering with database queries
 * - Novel status updates and publishing flow
 * - Database transactions and data integrity
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class NovelIntegrationTest {

    @Autowired
    private WebApplicationContext context;

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

    private User authorUser;
    private User regularUser;
    private String authorToken;
    private String regularUserToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test users
        createTestUsers();
    }

    /**
     * Test novel creation with database persistence
     */
    @Test
    void testCreateNovel_WithDatabasePersistence() throws Exception {
        // Given
        Map<String, Object> novelRequest = new HashMap<>();
        novelRequest.put("title", "Test Novel");
        novelRequest.put("synopsis", "A test novel description");
        novelRequest.put("categoryId", 1); // Fantasy category

        // When
        mockMvc.perform(post("/api/novels")
                .header("Authorization", "Bearer " + authorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novelRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.title").value("Test Novel"))
                .andExpect(jsonPath("$.data.synopsis").value("A test novel description"))
                .andExpect(jsonPath("$.data.authorId").value(authorUser.getUuid().toString()));

        // Then - Verify novel was persisted in database
        // Query database to verify novel was created by checking novels with pagination
        NovelSearchRequestDTO searchReq = new NovelSearchRequestDTO(0, 100, "createTime", "desc", null, null, null, null, null);
        List<Novel> allNovels = novelMapper.selectNovelsWithPagination(searchReq);
        
        // Verify the created novel has the expected properties
        Novel createdNovel = allNovels.stream()
                .filter(novel -> "Test Novel".equals(novel.getTitle()))
                .filter(novel -> authorUser.getUuid().equals(novel.getAuthorId()))
                .findFirst()
                .orElse(null);
        assertThat(createdNovel).isNotNull();
        assertThat(createdNovel.getTitle()).isEqualTo("Test Novel");
        assertThat(createdNovel.getSynopsis()).isEqualTo("A test novel description");
        assertThat(createdNovel.getAuthorId()).isEqualTo(authorUser.getUuid());
        assertThat(createdNovel.getCategoryId()).isEqualTo(1);
    }

    /**
     * Test novel retrieval with database query
     */
    @Test
    void testGetNovel_WithDatabaseQuery() throws Exception {
        // Given - Create novel in database
        Novel testNovel = createTestNovel("Database Novel", "A novel stored in database");
        novelMapper.insert(testNovel);

        // When
        mockMvc.perform(get("/api/novels/" + testNovel.getId())
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.title").value("Database Novel"))
                .andExpect(jsonPath("$.data.synopsis").value("A novel stored in database"))
                .andExpect(jsonPath("$.data.uuid").value(testNovel.getUuid().toString()));
    }

    /**
     * Test novel update with database persistence
     */
    @Test
    void testUpdateNovel_WithDatabasePersistence() throws Exception {
        // Given - Create novel in database
        Novel testNovel = createTestNovel("Original Title", "Original description");
        novelMapper.insert(testNovel);

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", "Updated Title");
        updateRequest.put("synopsis", "Updated description");
        // Removed status update as only admin can change novel status directly

        // When
        mockMvc.perform(put("/api/novels/" + testNovel.getId())
                .header("Authorization", "Bearer " + authorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.title").value("Updated Title"))
                .andExpect(jsonPath("$.data.synopsis").value("Updated description"));

        // Then - Verify update was persisted
        Novel updatedNovel = novelMapper.selectByPrimaryKey(testNovel.getId());
        assertThat(updatedNovel).isNotNull();
        assertThat(updatedNovel.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedNovel.getSynopsis()).isEqualTo("Updated description");
    }

    /**
     * Test novel search with database query
     */
    @Test
    void testSearchNovels_WithDatabaseQuery() throws Exception {
        // Given - Create multiple novels in database
        Novel novel1 = createTestNovel("Fantasy Adventure", "A fantasy novel");
        novel1.setCategoryId(1); // Fantasy category
        novelMapper.insert(novel1);

        Novel novel2 = createTestNovel("Sci-Fi Story", "A science fiction novel");
        novel2.setCategoryId(2); // Sci-Fi category
        novelMapper.insert(novel2);

        // When - Search by category
        mockMvc.perform(get("/api/novels")
                .param("category", "1")
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    /**
     * Test author permissions with database verification
     */
    @Test
    void testAuthorPermissions_WithDatabaseVerification() throws Exception {
        // Given - Create novel by author
        Novel authorNovel = createTestNovel("Author's Novel", "Only author can modify this");
        authorNovel.setAuthorId(authorUser.getUuid());
        novelMapper.insert(authorNovel);

        // When - Regular user tries to update author's novel
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", "Unauthorized Update");

        mockMvc.perform(put("/api/novels/" + authorNovel.getId())
                .header("Authorization", "Bearer " + regularUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test novel status update with database persistence
     */
    @Test
    void testNovelStatusUpdate_WithDatabasePersistence() throws Exception {
        // Given - Create admin user and draft novel
        User adminUser = createTestUser("admin@example.com", "adminuser");
        adminUser.setIsAdmin(true);
        userMapper.insert(adminUser);
        String adminToken = jwtUtil.generateAccessToken(adminUser);
        
        Novel draftNovel = createTestNovel("Draft Novel", "A novel in draft status");
        draftNovel.setStatus(0); // DRAFT status
        novelMapper.insert(draftNovel);

        Map<String, Object> statusRequest = new HashMap<>();
        statusRequest.put("status", "PUBLISHED");

        // When - Admin updates novel status
        mockMvc.perform(put("/api/novels/" + draftNovel.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()));

        // Then - Verify status was updated in database
        Novel statusUpdatedNovel = novelMapper.selectByPrimaryKey(draftNovel.getId());
        assertThat(statusUpdatedNovel).isNotNull();
        assertThat(statusUpdatedNovel.getStatus()).isEqualTo(2); // PUBLISHED status (enum value 2)
    }

    /**
     * Test novel listing with pagination and database query
     */
    @Test
    void testNovelListing_WithPaginationAndDatabaseQuery() throws Exception {
        // Given - Create multiple novels
        for (int i = 1; i <= 5; i++) {
            Novel novel = createTestNovel("Novel " + i, "Description " + i);
            novelMapper.insert(novel);
        }

        // When - Get paginated list
        mockMvc.perform(get("/api/novels")
                .param("page", "0")
                .param("size", "3")
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.size").value(3));
    }

    /**
     * Test database transaction rollback on error
     */
    @Test
    void testDatabaseTransactionRollback_OnNovelCreationError() throws Exception {
        // Given - Invalid novel data
        Map<String, Object> invalidRequest = new HashMap<>();
        invalidRequest.put("title", ""); // Empty title should fail validation
        invalidRequest.put("synopsis", "Valid description");

        // When
        mockMvc.perform(post("/api/novels")
                .header("Authorization", "Bearer " + authorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Then - Verify no novel was created in database
        // Query database to verify no novel with empty title was created
        NovelSearchRequestDTO searchReq = new NovelSearchRequestDTO(0, 100, "createTime", "desc", null, null, null, null, null);
        List<Novel> allNovels = novelMapper.selectNovelsWithPagination(searchReq);
        
        // Verify no novel with empty title exists
        boolean hasEmptyTitleNovel = allNovels.stream()
                .anyMatch(novel -> novel.getTitle() == null || novel.getTitle().trim().isEmpty());
        assertThat(hasEmptyTitleNovel).isFalse();
        
        // Also verify that no novel was created by this author
        long authorNovelCount = allNovels.stream()
                .filter(novel -> authorUser.getUuid().equals(novel.getAuthorId()))
                .count();
        assertThat(authorNovelCount).isEqualTo(0);
    }

    /**
     * Test novel rating and review count updates
     */
    @Test
    void testNovelRatingAndReviewCount_WithDatabaseUpdates() throws Exception {
        // Given - Create novel
        Novel testNovel = createTestNovel("Rating Test Novel", "A novel for testing ratings");
        testNovel.setAuthorId(authorUser.getUuid());
        novelMapper.insert(testNovel);

        // When - Create review (this should update novel rating and review count)
        Map<String, Object> reviewRequest = new HashMap<>();
        reviewRequest.put("novelId", testNovel.getId());
        reviewRequest.put("rating", 4);
        reviewRequest.put("title", "Good Novel");
        reviewRequest.put("content", "This is a good novel with interesting plot.");
        reviewRequest.put("isSpoiler", false);

        mockMvc.perform(post("/api/reviews")
                .header("Authorization", "Bearer " + regularUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated());

        // Then - Verify novel rating and review count were updated
        Novel updatedNovel = novelMapper.selectByPrimaryKey(testNovel.getId());
        assertThat(updatedNovel).isNotNull();
        assertThat(updatedNovel.getAvgRating()).isEqualTo(4.0f);
        assertThat(updatedNovel.getReviewCnt()).isEqualTo(1);
    }

    /**
     * Test novel vote count updates
     */
    @Test
    void testNovelVoteCount_WithDatabaseUpdates() throws Exception {
        // Given - Create novel
        Novel testNovel = createTestNovel("Vote Test Novel", "A novel for testing votes");
        testNovel.setAuthorId(authorUser.getUuid());
        novelMapper.insert(testNovel);

        // When - Vote for novel
        mockMvc.perform(post("/api/novels/" + testNovel.getId() + "/vote")
                .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.voteCount").value(1));

        // Then - Verify novel vote count was updated
        Novel votedNovel = novelMapper.selectByPrimaryKey(testNovel.getId());
        assertThat(votedNovel).isNotNull();
        assertThat(votedNovel.getVoteCnt()).isEqualTo(1);
    }

    /**
     * Test novel search with rating filter
     */
    @Test
    void testNovelSearch_WithRatingFilter() throws Exception {
        // Given - Create novels with different ratings
        Novel highRatedNovel = createTestNovel("High Rated Novel", "A highly rated novel");
        highRatedNovel.setAuthorId(authorUser.getUuid());
        highRatedNovel.setAvgRating(4.5f);
        highRatedNovel.setReviewCnt(10);
        novelMapper.insert(highRatedNovel);

        Novel lowRatedNovel = createTestNovel("Low Rated Novel", "A low rated novel");
        lowRatedNovel.setAuthorId(authorUser.getUuid());
        lowRatedNovel.setAvgRating(2.0f);
        lowRatedNovel.setReviewCnt(5);
        novelMapper.insert(lowRatedNovel);

        // When - Search by minimum rating
        mockMvc.perform(get("/api/novels")
                .param("minRating", "4.0")
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    /**
     * Test novel statistics with rating and vote data
     */
    @Test
    void testNovelStatistics_WithRatingAndVoteData() throws Exception {
        // Given - Create novel with statistics
        Novel statsNovel = createTestNovel("Stats Novel", "A novel with statistics");
        statsNovel.setAuthorId(authorUser.getUuid());
        statsNovel.setAvgRating(4.2f);
        statsNovel.setReviewCnt(15);
        statsNovel.setVoteCnt(25);
        statsNovel.setViewCnt(1000L);
        novelMapper.insert(statsNovel);

        // When - Get novel details
        mockMvc.perform(get("/api/novels/" + statsNovel.getId())
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.avgRating").value(4.2))
                .andExpect(jsonPath("$.data.reviewCnt").value(15))
                .andExpect(jsonPath("$.data.voteCnt").value(25))
                .andExpect(jsonPath("$.data.viewCnt").value(1000));
    }

    /**
     * Test novel ranking by rating and votes
     */
    @Test
    void testNovelRanking_ByRatingAndVotes() throws Exception {
        // Given - Create novels with different ratings and votes
        Novel topNovel = createTestNovel("Top Novel", "The best novel");
        topNovel.setAuthorId(authorUser.getUuid());
        topNovel.setAvgRating(4.8f);
        topNovel.setReviewCnt(50);
        topNovel.setVoteCnt(100);
        novelMapper.insert(topNovel);

        Novel averageNovel = createTestNovel("Average Novel", "An average novel");
        averageNovel.setAuthorId(authorUser.getUuid());
        averageNovel.setAvgRating(3.0f);
        averageNovel.setReviewCnt(10);
        averageNovel.setVoteCnt(20);
        novelMapper.insert(averageNovel);

        // When - Search novels sorted by rating
        mockMvc.perform(get("/api/novels")
                .param("sort", "avgRating")
                .param("order", "desc")
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    /**
     * Test novel approval workflow
     */
    @Test
    void testNovelApprovalWorkflow_WithDatabaseUpdates() throws Exception {
        // Given - Create admin user and draft novel
        User adminUser = createTestUser("admin@example.com", "adminuser");
        adminUser.setIsAdmin(true);
        userMapper.insert(adminUser);
        String adminToken = jwtUtil.generateAccessToken(adminUser);
        
        Novel draftNovel = createTestNovel("Draft Novel", "A novel in draft status");
        draftNovel.setStatus(0); // DRAFT status
        novelMapper.insert(draftNovel);

        // When - Author submits for review
        mockMvc.perform(post("/api/novels/" + draftNovel.getId() + "/submit-review")
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()));

        // Then - Admin approves novel
        mockMvc.perform(post("/api/novels/" + draftNovel.getId() + "/approve")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()));

        // Verify novel status was updated
        Novel approvedNovel = novelMapper.selectByPrimaryKey(draftNovel.getId());
        assertThat(approvedNovel).isNotNull();
        assertThat(approvedNovel.getStatus()).isEqualTo(2); // PUBLISHED status
    }

    /**
     * Helper method to create test users
     */
    private void createTestUsers() {
        // Create author user
        authorUser = new User();
        authorUser.setUuid(UUID.randomUUID());
        authorUser.setEmail("author@example.com");
        authorUser.setUsername("author");
        authorUser.setHashPassword(passwordEncoder.encode("password123"));
        authorUser.setEmailVerified(true);
        authorUser.setAvatarUrl("https://example.com/avatar.jpg");
        authorUser.setStatus(1); // Active status
        authorUser.setGender(1);
        authorUser.setCreateTime(new Date());
        authorUser.setUpdateTime(new Date());
        authorUser.setLastLogin(new Date());
        authorUser.setLastActive(new Date());
        authorUser.setIsAuthor(true);
        authorUser.setIsAdmin(false);
        authorUser.setLevel(1);
        authorUser.setExp(0.0f);
        authorUser.setYuan(0.0f);
        authorUser.setReadTime(0.0f);
        authorUser.setReadBookNum(0);
        userMapper.insert(authorUser);

        // Create regular user
        regularUser = new User();
        regularUser.setUuid(UUID.randomUUID());
        regularUser.setEmail("user@example.com");
        regularUser.setUsername("user");
        regularUser.setHashPassword(passwordEncoder.encode("password123"));
        regularUser.setEmailVerified(true);
        regularUser.setAvatarUrl("https://example.com/avatar.jpg");
        regularUser.setStatus(1); // Active status
        regularUser.setGender(1);
        regularUser.setCreateTime(new Date());
        regularUser.setUpdateTime(new Date());
        regularUser.setLastLogin(new Date());
        regularUser.setLastActive(new Date());
        regularUser.setIsAuthor(false);
        regularUser.setIsAdmin(false);
        regularUser.setLevel(1);
        regularUser.setExp(0.0f);
        regularUser.setYuan(100.0f);
        regularUser.setReadTime(0.0f);
        regularUser.setReadBookNum(0);
        userMapper.insert(regularUser);

        // Generate tokens
        authorToken = jwtUtil.generateAccessToken(authorUser);
        regularUserToken = jwtUtil.generateAccessToken(regularUser);
    }

    /**
     * Helper method to create a test user
     */
    private User createTestUser(String email, String username) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(true);
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setStatus(1); // Active status
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
        // Use truly unique ID based on UUID hash to avoid collisions
        novel.setId(Math.abs(UUID.randomUUID().hashCode()));
        novel.setUuid(UUID.randomUUID());
        novel.setTitle(title);
        novel.setSynopsis(description);
        novel.setAuthorId(authorUser.getUuid());
        novel.setCategoryId(1); // Fantasy category
        novel.setStatus(0); // DRAFT status
        novel.setVoteCnt(0); // Initialize vote count to 0
        novel.setCreateTime(new Date());
        novel.setUpdateTime(new Date());
        return novel;
    }
}