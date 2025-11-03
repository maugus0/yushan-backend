package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.User;
import com.yushan.backend.service.AuthService;
import com.yushan.backend.service.MailService;
import com.yushan.backend.util.JwtUtil;
import com.yushan.backend.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private MailService mailService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean

    private UserRegistrationRequestDTO registrationDTO;
    private UserRegistrationResponseDTO userAuthResponseDTO;
    private UserLoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        registrationDTO = new UserRegistrationRequestDTO();
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setUsername("testuser");
        registrationDTO.setPassword("password123");
        registrationDTO.setCode("123456");

        userAuthResponseDTO = new UserRegistrationResponseDTO();
        userAuthResponseDTO.setAccessToken("fake-access-token");
        userAuthResponseDTO.setRefreshToken("fake-refresh-token");
        userAuthResponseDTO.setUuid(UUID.randomUUID().toString());
        userAuthResponseDTO.setUsername("testuser");
        userAuthResponseDTO.setEmail("test@example.com");

        loginRequest = new UserLoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_Success() throws Exception {
        // Given
        when(mailService.verifyEmail(registrationDTO.getEmail(), registrationDTO.getCode())).thenReturn(true);
        when(authService.registerAndCreateResponse(any(UserRegistrationRequestDTO.class))).thenReturn(userAuthResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("fake-access-token"));
    }

    @Test
    void register_InvalidCode() throws Exception {
        // Given
        when(mailService.verifyEmail(registrationDTO.getEmail(), registrationDTO.getCode())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid verification code or code expired"));
    }

    @Test
    void login_Success() throws Exception {
        // Given
        when(authService.loginAndCreateResponse(loginRequest.getEmail(), loginRequest.getPassword())).thenReturn(userAuthResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.email").value(loginRequest.getEmail()));
    }

    @Test
    void logout_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("JWT tokens are stateless and cannot be invalidated server-side. Client should discard tokens."));
    }

    @Test
    void refresh_Success() throws Exception {
        // Given
        RefreshRequestDTO refreshRequest = new RefreshRequestDTO();
        refreshRequest.setRefreshToken("fake-refresh-token");
        when(authService.refreshToken(anyString())).thenReturn(userAuthResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void sendEmail_Success() throws Exception {
        // Given
        EmailVerificationRequestDTO emailRequest = new EmailVerificationRequestDTO();
        emailRequest.setEmail("new-user@example.com");
        when(userMapper.selectByEmail(emailRequest.getEmail())).thenReturn(null);
        doNothing().when(mailService).sendVerificationCode(emailRequest.getEmail());

        // When & Then
        mockMvc.perform(post("/api/auth/send-email")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Verification code sent successfully"));

        verify(mailService, times(1)).sendVerificationCode(emailRequest.getEmail());
    }

    @Test
    void sendEmail_EmailAlreadyExists() throws Exception {
        // Given
        EmailVerificationRequestDTO emailRequest = new EmailVerificationRequestDTO();
        emailRequest.setEmail("existing-user@example.com");
        when(userMapper.selectByEmail(emailRequest.getEmail())).thenReturn(new User());

        // When & Then
        mockMvc.perform(post("/api/auth/send-email")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));

        verify(mailService, never()).sendVerificationCode(anyString());
    }
}

