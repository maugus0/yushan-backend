package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AuthorUpgradeRequestDTO;
import com.yushan.backend.dto.EmailVerificationRequestDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.service.AuthorService;
import com.yushan.backend.service.MailService;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(AuthorController.class)
@EnableMethodSecurity
class AuthorControllerTest {

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithCustomUserDetailsSecurityContextFactory.class)
    public @interface WithCustomUserDetails {
        String email() default "test@example.com";
    }

    public static class WithCustomUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithCustomUserDetails> {
        @Override
        public SecurityContext createSecurityContext(WithCustomUserDetails annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            
            // Mock User entity
            User user = new User();
            user.setUuid(UUID.randomUUID());
            user.setEmail(annotation.email());
            user.setUsername("testuser");
            user.setHashPassword("password");
            user.setIsAuthor(false);
            user.setIsAdmin(false);

            // Mock CustomUserDetails
            CustomUserDetails userDetails = new CustomUserDetails(user);
            
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            context.setAuthentication(auth);
            return context;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private MailService mailService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail(testEmail);
        testUser.setUsername("testuser");
        testUser.setIsAuthor(false);
        testUser.setIsAdmin(false);
    }

    @Test
    @WithMockUser
    void sendEmailAuthorVerification_Success() throws Exception {
        // Given
        EmailVerificationRequestDTO request = new EmailVerificationRequestDTO();
        request.setEmail(testEmail);

        when(userMapper.selectByEmail(testEmail)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("Verification code sent successfully"));

        verify(mailService).sendVerificationCode(testEmail);
    }

    @Test
    @WithMockUser
    void sendEmailAuthorVerification_UserNotFound() throws Exception {
        // Given
        EmailVerificationRequestDTO request = new EmailVerificationRequestDTO();
        request.setEmail(testEmail);

        when(userMapper.selectByEmail(testEmail)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(mailService, never()).sendVerificationCode(anyString());
    }

    @Test
    @WithMockUser
    void sendEmailAuthorVerification_UserAlreadyAuthor() throws Exception {
        // Given
        EmailVerificationRequestDTO request = new EmailVerificationRequestDTO();
        request.setEmail(testEmail);
        testUser.setIsAuthor(true);

        when(userMapper.selectByEmail(testEmail)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("User is already an author"));

        verify(mailService, never()).sendVerificationCode(anyString());
    }

    @Test
    @WithMockUser
    void sendEmailAuthorVerification_InvalidEmail() throws Exception {
        // Given
        EmailVerificationRequestDTO request = new EmailVerificationRequestDTO();
        request.setEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Email must be valid"));

        verify(mailService, never()).sendVerificationCode(anyString());
    }

    @Test
    @WithMockUser
    void sendEmailAuthorVerification_EmptyEmail() throws Exception {
        // Given
        EmailVerificationRequestDTO request = new EmailVerificationRequestDTO();
        request.setEmail("");

        // When & Then
        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Email is required"));

        verify(mailService, never()).sendVerificationCode(anyString());
    }

    @Test
    void sendEmailAuthorVerification_Unauthorized() throws Exception {
        // Given
        EmailVerificationRequestDTO request = new EmailVerificationRequestDTO();
        request.setEmail(testEmail);

        // When & Then
        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomUserDetails(email = "test@example.com")
    void upgradeToAuthor_Success() throws Exception {
        // Given
        AuthorUpgradeRequestDTO request = new AuthorUpgradeRequestDTO();
        request.setVerificationCode("123456");

        UserProfileResponseDTO expectedResponse = new UserProfileResponseDTO();
        expectedResponse.setEmail(testEmail);
        expectedResponse.setUsername("testuser");
        expectedResponse.setIsAuthor(true);

        when(authorService.upgradeToAuthor(anyString(), anyString())).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("User upgraded to author successfully"))
                .andExpect(jsonPath("$.data.email").value(testEmail))
                .andExpect(jsonPath("$.data.isAuthor").value(true));
    }

    @Test
    @WithMockUser
    void upgradeToAuthor_InvalidVerificationCode() throws Exception {
        // Given
        AuthorUpgradeRequestDTO request = new AuthorUpgradeRequestDTO();
        request.setVerificationCode("invalid");

        // When & Then - This will trigger validation error, not service error
        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Verification code must be exactly 6 characters"));
    }

    @Test
    @WithCustomUserDetails(email = "test@example.com")
    void upgradeToAuthor_InvalidVerificationCodeLength() throws Exception {
        // Given
        AuthorUpgradeRequestDTO request = new AuthorUpgradeRequestDTO();
        request.setVerificationCode("123"); // Too short

        // When & Then
        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Verification code must be exactly 6 characters"));
    }

    @Test
    @WithCustomUserDetails(email = "test@example.com")
    void upgradeToAuthor_EmptyVerificationCode() throws Exception {
        // Given
        AuthorUpgradeRequestDTO request = new AuthorUpgradeRequestDTO();
        request.setVerificationCode("");

        // When & Then
        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Verification code is required")))
                .andExpect(jsonPath("$.message").value(containsString("Verification code must be exactly 6 characters")));
    }

    @Test
    void upgradeToAuthor_Unauthorized() throws Exception {
        // Given
        AuthorUpgradeRequestDTO request = new AuthorUpgradeRequestDTO();
        request.setVerificationCode("123456");

        // When & Then
        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
