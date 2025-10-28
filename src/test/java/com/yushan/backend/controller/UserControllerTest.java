package com.yushan.backend.controller;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.UserService;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private User user;
    private UserProfileResponseDTO profileDTO;
    private UserProfileUpdateRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        user = new User();
        user.setUuid(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setYuan(100.0f);
        
        profileDTO = new UserProfileResponseDTO();
        profileDTO.setUuid(userId.toString());
        profileDTO.setUsername("testuser");
        profileDTO.setEmail("test@example.com");
        
        updateRequest = new UserProfileUpdateRequestDTO();
        updateRequest.setUsername("newusername");
        updateRequest.setAvatarBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
    }

    @Test
    @DisplayName("Test getCurrentUserProfile - Success")
    void testGetCurrentUserProfileSuccess() {
        // Given
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        when(userService.getUserProfile(userId)).thenReturn(profileDTO);
        
        // When
        ApiResponse<UserProfileResponseDTO> response = 
            userController.getCurrentUserProfile(authentication);
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("User profile retrieved successfully", response.getMessage());
        assertEquals(profileDTO, response.getData());
        verify(userService).getUserProfile(userId);
    }

    @Test
    @DisplayName("Test getCurrentUserProfile - Not authenticated")
    void testGetCurrentUserProfileNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // When & Then
        assertThrows(Exception.class, () -> {
            userController.getCurrentUserProfile(authentication);
        });
    }

    @Test
    @DisplayName("Test getCurrentUserProfile - User details with email fallback")
    void testGetCurrentUserProfileWithEmailFallback() {
        // Given
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetails.getUserId()).thenReturn(null);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userMapper.selectByEmail("test@example.com")).thenReturn(user);
        when(userService.getUserProfile(userId)).thenReturn(profileDTO);
        
        // When
        ApiResponse<UserProfileResponseDTO> response = 
            userController.getCurrentUserProfile(authentication);
        
        // Then
        assertNotNull(response);
        verify(userService).getUserProfile(userId);
    }

    @Test
    @DisplayName("Test updateProfile - Success without email change")
    void testUpdateProfileSuccess() {
        // Given
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        
        UserProfileUpdateResponseDTO updateResponse = new UserProfileUpdateResponseDTO();
        updateResponse.setProfile(profileDTO);
        updateResponse.setEmailChanged(false);
        
        when(userService.updateUserProfileSelective(userId, updateRequest)).thenReturn(updateResponse);
        
        // When
        ApiResponse<UserProfileUpdateResponseDTO> response = 
            userController.updateProfile(userId, updateRequest, authentication);
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Profile updated successfully", response.getMessage());
        verify(userService).updateUserProfileSelective(userId, updateRequest);
    }

    @Test
    @DisplayName("Test updateProfile - Success with email change")
    void testUpdateProfileWithEmailChange() {
        // Given
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        
        UserProfileUpdateResponseDTO updateResponse = new UserProfileUpdateResponseDTO();
        updateResponse.setProfile(profileDTO);
        updateResponse.setEmailChanged(true);
        
        when(userService.updateUserProfileSelective(userId, updateRequest)).thenReturn(updateResponse);
        when(userMapper.selectByPrimaryKey(userId)).thenReturn(user);
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("newRefreshToken");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(3600L);
        
        // When
        ApiResponse<UserProfileUpdateResponseDTO> response = 
            userController.updateProfile(userId, updateRequest, authentication);
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getAccessToken());
        assertNotNull(response.getData().getRefreshToken());
        verify(userMapper).selectByPrimaryKey(userId);
    }

    @Test
    @DisplayName("Test updateProfile - Not authorized")
    void testUpdateProfileNotAuthorized() {
        // Given
        UUID differentUserId = UUID.randomUUID();
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        
        // When & Then
        assertThrows(Exception.class, () -> {
            userController.updateProfile(differentUserId, updateRequest, authentication);
        });
    }

    @Test
    @DisplayName("Test sendEmailChangeVerification - Success")
    void testSendEmailChangeVerificationSuccess() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        
        EmailVerificationRequestDTO emailRequest = new EmailVerificationRequestDTO();
        emailRequest.setEmail("newemail@example.com");
        
        doNothing().when(userService).sendEmailChangeVerification("newemail@example.com");
        
        // When
        ApiResponse<String> response = 
            userController.sendEmailChangeVerification(emailRequest, authentication);
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Verification code sent successfully", response.getMessage());
        verify(userService).sendEmailChangeVerification("newemail@example.com");
    }

    @Test
    @DisplayName("Test getUserDetail - Success")
    void testGetUserDetailSuccess() {
        // Given
        when(userService.getUserProfile(userId)).thenReturn(profileDTO);
        
        // When
        ApiResponse<UserProfileResponseDTO> response = 
            userController.getUserDetail(userId);
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("User profile retrieved successfully", response.getMessage());
        assertEquals(profileDTO, response.getData());
        verify(userService).getUserProfile(userId);
    }
}

