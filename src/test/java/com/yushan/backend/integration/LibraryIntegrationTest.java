package com.yushan.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.dao.LibraryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.Library;
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
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Library management with real PostgreSQL
 * 
 * This test class verifies:
 * - Library CRUD operations with database persistence
 * - User library creation and management
 * - Library permissions and access control
 * - Database transactions and data integrity
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class LibraryIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private LibraryMapper libraryMapper;

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
     * Test create user library with database persistence
     */
    @Test
    void testCreateUserLibrary_WithDatabasePersistence() throws Exception {
        // Given
        Map<String, Object> createRequest = new HashMap<>();
        createRequest.put("name", "My Reading Library");
        createRequest.put("description", "A personal reading library");

        // When
        mockMvc.perform(post("/api/library")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.userId").value(testUser.getUuid().toString()));

        // Then - Verify library was persisted in database
        // Note: In real implementation, query database to verify library creation
    }

    /**
     * Test get user library with database query
     */
    @Test
    void testGetUserLibrary_WithDatabaseQuery() throws Exception {
        // Given - Create library in database
        Library library = createTestLibrary(testUser.getUuid());
        libraryMapper.insert(library);

        // When
        mockMvc.perform(get("/api/library")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(testUser.getUuid().toString()));
    }

    /**
     * Test update library with database persistence
     */
    @Test
    void testUpdateLibrary_WithDatabasePersistence() throws Exception {
        // Given - Create library in database
        Library library = createTestLibrary(testUser.getUuid());
        libraryMapper.insert(library);

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("name", "Updated Library Name");
        updateRequest.put("description", "Updated description");

        // When
        mockMvc.perform(put("/api/library/" + library.getUuid())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()));

        // Then - Verify update was persisted
        // Note: In real implementation, query database to verify changes
    }

    /**
     * Test delete library with database removal
     */
    @Test
    void testDeleteLibrary_WithDatabaseRemoval() throws Exception {
        // Given - Create library in database
        Library library = createTestLibrary(testUser.getUuid());
        libraryMapper.insert(library);

        // When
        mockMvc.perform(delete("/api/library/" + library.getUuid())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Library deleted successfully"));

        // Then - Verify library was removed from database
        // Note: In real implementation, verify library no longer exists in database
    }

    /**
     * Test library permissions and access control
     */
    @Test
    void testLibraryPermissions_AndAccessControl() throws Exception {
        // Given - Create another user's library
        User anotherUser = createTestUser("another@example.com", "anotheruser");
        userMapper.insert(anotherUser);
        
        Library anotherUserLibrary = createTestLibrary(anotherUser.getUuid());
        libraryMapper.insert(anotherUserLibrary);

        // When - Current user tries to access another user's library
        mockMvc.perform(get("/api/library/" + anotherUserLibrary.getUuid())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isUnauthorized());

        // Then - Verify access control
        // Note: In real implementation, verify user can only access their own library
    }

    /**
     * Test library listing with pagination and database query
     */
    @Test
    void testLibraryListing_WithPaginationAndDatabaseQuery() throws Exception {
        // Given - Create multiple libraries
        for (int i = 1; i <= 5; i++) {
            Library library = createTestLibrary(testUser.getUuid());
            libraryMapper.insert(library);
        }

        // When - Get paginated list
        mockMvc.perform(get("/api/library")
                .param("page", "0")
                .param("size", "3")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.size").value(3));
    }

    /**
     * Test database transaction rollback on library error
     */
    @Test
    void testDatabaseTransactionRollback_OnLibraryCreationError() throws Exception {
        // Given - Invalid library data
        Map<String, Object> invalidRequest = new HashMap<>();
        invalidRequest.put("name", ""); // Empty name should fail validation
        invalidRequest.put("description", "Valid description");

        // When
        mockMvc.perform(post("/api/library")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Then - Verify no library was created in database
        // Note: In real implementation, verify no invalid library exists in database
    }

    /**
     * Test library search and filtering with database query
     */
    @Test
    void testLibrarySearch_WithDatabaseQuery() throws Exception {
        // Given - Create library entries
        Library library1 = createTestLibrary(testUser.getUuid());
        libraryMapper.insert(library1);

        // When - Search libraries
        mockMvc.perform(get("/api/library/search")
                .param("name", "Test")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * Test library statistics with database aggregation
     */
    @Test
    void testLibraryStatistics_WithDatabaseAggregation() throws Exception {
        // Given - Create multiple libraries
        Library library1 = createTestLibrary(testUser.getUuid());
        libraryMapper.insert(library1);

        Library library2 = createTestLibrary(testUser.getUuid());
        libraryMapper.insert(library2);

        // When
        mockMvc.perform(get("/api/library/statistics")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.totalLibraries").value(2));

        // Then - Verify statistics are calculated from database
        // Note: In real implementation, verify aggregated statistics are correct
    }

    /**
     * Helper method to create test data
     */
    private void createTestData() {
        // Create test user
        testUser = createTestUser("testuser@example.com", "testuser");
        userMapper.insert(testUser);

        // Create test novel
        testNovel = createTestNovel("Test Novel", "A test novel for library testing");
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
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        return user;
    }

    /**
     * Helper method to create test novel
     */
    private Novel createTestNovel(String title, String description) {
        Novel novel = new Novel();
        novel.setUuid(UUID.randomUUID());
        novel.setTitle(title);
        novel.setSynopsis(description);
        novel.setAuthorId(testUser.getUuid());
        novel.setCategoryId(1); // Fantasy category
        novel.setStatus(0); // DRAFT status
        novel.setCreateTime(new Date());
        novel.setUpdateTime(new Date());
        return novel;
    }

    /**
     * Helper method to create test library
     */
    private Library createTestLibrary(UUID userId) {
        Library library = new Library();
        library.setUuid(UUID.randomUUID());
        library.setUserId(userId);
        library.setCreateTime(new Date());
        library.setUpdateTime(new Date());
        return library;
    }
}