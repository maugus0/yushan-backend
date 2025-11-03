package com.yushan.backend.service;

import com.yushan.backend.dao.LibraryMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserRegistrationRequestDTO;
import com.yushan.backend.dto.UserRegistrationResponseDTO;
import com.yushan.backend.entity.Library;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.Gender;
import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private LibraryMapper libraryMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Mock
    private EXPService expService;

    private UserRegistrationRequestDTO registrationDTO;
    private User testUser;
    private UUID userUuid;
    private String userEmail;
    private String userPassword;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "accessTokenExpiration", 3600L);

        userUuid = UUID.randomUUID();
        userEmail = "test@example.com";
        userPassword = "password123";

        registrationDTO = new UserRegistrationRequestDTO();
        registrationDTO.setEmail(userEmail);
        registrationDTO.setUsername("testuser");
        registrationDTO.setPassword(userPassword);
        registrationDTO.setGender(Gender.MALE);
        registrationDTO.setBirthday(new Date());

        testUser = new User();
        testUser.setUuid(userUuid);
        testUser.setEmail(userEmail);
        testUser.setUsername("testuser");
        testUser.setHashPassword(BCrypt.hashpw(userPassword, BCrypt.gensalt()));
        testUser.setGender(Gender.MALE.getCode());
        testUser.setAvatarUrl(Gender.MALE.getAvatarUrl());
        testUser.setStatus(0);
        testUser.setIsAuthor(false);
        testUser.setIsAdmin(false);
        testUser.setCreateTime(new Date());
        testUser.setUpdateTime(new Date());
        testUser.setExp(0f);
        testUser.setYuan(0f);
    }

    @Test
    void register_Success() {
        // Given
        when(userMapper.selectByEmail(userEmail)).thenReturn(null);

        // When
        User registeredUser = authService.register(registrationDTO);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertThat(capturedUser.getEmail()).isEqualTo(registrationDTO.getEmail());
        assertThat(capturedUser.getUsername()).isEqualTo(registrationDTO.getUsername());
        assertThat(BCrypt.checkpw(userPassword, capturedUser.getHashPassword())).isTrue();
        assertThat(capturedUser.getGender()).isEqualTo(Gender.MALE.getCode());
        assertThat(capturedUser.getAvatarUrl()).isEqualTo(Gender.MALE.getAvatarUrl());

        ArgumentCaptor<Library> libraryCaptor = ArgumentCaptor.forClass(Library.class);
        verify(libraryMapper).insertSelective(libraryCaptor.capture());
        Library capturedLibrary = libraryCaptor.getValue();
        assertThat(capturedLibrary.getUserId()).isEqualTo(capturedUser.getUuid());

        assertThat(registeredUser).isEqualTo(capturedUser);
    }

    @Test
    void register_EmailAlreadyExists_ThrowsValidationException() {
        // Given
        when(userMapper.selectByEmail(userEmail)).thenReturn(new User());

        // When & Then
        assertThatThrownBy(() -> authService.register(registrationDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("email was registered");

        verify(userMapper, never()).insert(any(User.class));
        verify(libraryMapper, never()).insertSelective(any(Library.class));
    }

    @Test
    void registerAndCreateResponse_Success() {
        // Given
        when(userMapper.selectByEmail(userEmail)).thenReturn(null);
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("fake-access-token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("fake-refresh-token");

        // When
        UserRegistrationResponseDTO response = authService.registerAndCreateResponse(registrationDTO);

        // Then
        verify(userMapper).insert(any(User.class));
        verify(libraryMapper).insertSelective(any(Library.class));
        verify(jwtUtil).generateAccessToken(any(User.class));
        verify(jwtUtil).generateRefreshToken(any(User.class));

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(userEmail);
        assertThat(response.getAccessToken()).isEqualTo("fake-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("fake-refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
    }

    @Test
    void login_Success() {
        // Given
        when(userMapper.selectByEmail(userEmail)).thenReturn(testUser);

        // When
        User loggedInUser = authService.login(userEmail, userPassword);

        // Then
        assertThat(loggedInUser).isEqualTo(testUser);
    }

    @Test
    void login_InvalidPassword_ThrowsValidationException() {
        // Given
        when(userMapper.selectByEmail(userEmail)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> authService.login(userEmail, "wrongpassword"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void login_UserNotFound_ThrowsValidationException() {
        // Given
        when(userMapper.selectByEmail(userEmail)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authService.login(userEmail, userPassword))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void loginAndCreateResponse_Success() {
        // Given
        when(userMapper.selectByEmail(userEmail)).thenReturn(testUser);
        when(jwtUtil.generateAccessToken(testUser)).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken(testUser)).thenReturn("new-refresh-token");

        // When
        UserRegistrationResponseDTO response = authService.loginAndCreateResponse(userEmail, userPassword);

        // Then
        verify(userMapper).updateByPrimaryKeySelective(any(User.class));

        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isEqualTo(userUuid.toString());
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void refreshToken_Success() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(refreshToken)).thenReturn(userEmail);
        when(jwtUtil.extractUserId(refreshToken)).thenReturn(userUuid.toString());
        when(userMapper.selectByEmail(userEmail)).thenReturn(testUser);
        when(jwtUtil.generateAccessToken(testUser)).thenReturn("new-access-token-from-refresh");
        when(jwtUtil.generateRefreshToken(testUser)).thenReturn("new-refresh-token-from-refresh");

        // When
        UserRegistrationResponseDTO response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token-from-refresh");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token-from-refresh");
        assertThat(response.getUuid()).isEqualTo(userUuid.toString());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsValidationException() {
        // Given
        String invalidToken = "invalid-token";
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(invalidToken))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void refreshToken_NotARefreshToken_ThrowsValidationException() {
        // Given
        String accessToken = "access-token";
        when(jwtUtil.validateToken(accessToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(accessToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(accessToken))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Token is not a refresh token");
    }

    @Test
    void refreshToken_UserNotFound_ThrowsValidationException() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(refreshToken)).thenReturn(userEmail);
        when(userMapper.selectByEmail(userEmail)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(ValidationException.class)
                .hasMessage("User not found or token mismatch");
    }

    @Test
    void refreshToken_TokenMismatch_ThrowsValidationException() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(refreshToken)).thenReturn(userEmail);
        when(jwtUtil.extractUserId(refreshToken)).thenReturn(UUID.randomUUID().toString()); // Mismatched UUID
        when(userMapper.selectByEmail(userEmail)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(ValidationException.class)
                .hasMessage("User not found or token mismatch");
    }
}
