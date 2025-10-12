package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.enums.UserStatus;
import com.yushan.backend.service.AdminService;
import com.yushan.backend.service.UserService;
import com.yushan.backend.util.JwtUtil;
import com.yushan.backend.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@EnableMethodSecurity
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RedisUtil redisUtil;

    private AdminPromoteRequestDTO request;
    private UserProfileResponseDTO response;

    @BeforeEach
    void setUp() {
        request = new AdminPromoteRequestDTO();
        request.setEmail("test@example.com");

        response = new UserProfileResponseDTO();
        response.setEmail("test@example.com");
        response.setUsername("testuser");
        response.setIsAdmin(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_Success() throws Exception {
        // Given
        when(adminService.promoteToAdmin(anyString())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/admin/promote-to-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("User promoted to admin successfully"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.isAdmin").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_UserNotFound() throws Exception {
        // Given
        when(adminService.promoteToAdmin(anyString()))
                .thenThrow(new IllegalArgumentException("User not found with email: test@example.com"));

        // When & Then
        mockMvc.perform(post("/api/admin/promote-to-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("User not found with email: test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_UserAlreadyAdmin() throws Exception {
        // Given
        when(adminService.promoteToAdmin(anyString()))
                .thenThrow(new IllegalArgumentException("User is already an admin"));

        // When & Then
        mockMvc.perform(post("/api/admin/promote-to-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("User is already an admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToAdmin_InvalidEmail() throws Exception {
        // Given
        request.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/admin/promote-to-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Email must be valid"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void promoteToAdmin_Forbidden() throws Exception {
        // Given
        AdminPromoteRequestDTO request = new AdminPromoteRequestDTO();
        request.setEmail("test@example.com");
        
        // When & Then
        mockMvc.perform(post("/api/admin/promote-to-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void promoteToAdmin_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/promote-to-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnFilteredAndPaginatedList() throws Exception {
        // Given
        PageResponseDTO<UserProfileResponseDTO> mockPage = new PageResponseDTO<>(Collections.emptyList(), 0L, 0, 10);
        when(adminService.listUsers(any(AdminUserFilterDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/admin/users?isAuthor=true&status=NORMAL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data.totalElements").value(0));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserDetail_shouldReturnFullUserProfile() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserProfileResponseDTO mockProfile = new UserProfileResponseDTO();
        mockProfile.setUuid(userId.toString());
        mockProfile.setEmail("detail@example.com");
        when(adminService.getUserDetail(userId)).thenReturn(mockProfile);

        // When & Then
        mockMvc.perform(get("/api/admin/users/{uuid}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User detail retrieved successfully"))
                .andExpect(jsonPath("$.data.email").value("detail@example.com"));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldCallServiceWithCorrectParameters() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        AdminUpdateUserDTO requestBody = new AdminUpdateUserDTO();
        requestBody.setStatus(UserStatus.BANNED);

        // When & Then
        mockMvc.perform(put("/api/admin/users/{uuid}/status", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User status updated successfully"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_whenNotAdmin_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }
}
