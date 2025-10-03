package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AdminPromoteRequestDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.service.AdminService;
import com.yushan.backend.service.UserService;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@EnableMethodSecurity
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
                .andExpect(jsonPath("$.code").value(0))
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
}
