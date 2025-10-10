package com.yushan.backend.interceptor;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService;
import com.yushan.backend.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserActivityInterceptorTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private CustomUserDetailsService.CustomUserDetails userDetails;

    @InjectMocks
    private UserActivityInterceptor userActivityInterceptor;

    private UUID testUserId;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUserId = UUID.randomUUID();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    void preHandle_shouldReturnTrue() throws Exception {
        // Given
        mockRequest.setRequestURI("/api/test");
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        boolean result = userActivityInterceptor.preHandle(mockRequest, mockResponse, null);

        // Then
        assertTrue(result);
    }

    @Test
    void preHandle_whenNotAuthenticated_shouldNotUpdateUserActivity() throws Exception {
        // Given
        mockRequest.setRequestURI("/api/test");
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        userActivityInterceptor.preHandle(mockRequest, mockResponse, null);

        // Then
        verifyNoInteractions(userMapper);
        verifyNoInteractions(redisUtil);
    }

    @Test
    void preHandle_whenAnonymousUser_shouldNotUpdateUserActivity() throws Exception {
        // Given
        mockRequest.setRequestURI("/api/test");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // When
        userActivityInterceptor.preHandle(mockRequest, mockResponse, null);

        // Then
        verifyNoInteractions(userMapper);
        verifyNoInteractions(redisUtil);
    }

    @Test
    void preHandle_whenValidUserAndShouldUpdate_shouldUpdateUserActivity() throws Exception {
        // Given
        mockRequest.setRequestURI("/api/test");
        mockRequest.setMethod("GET");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());

        when(redisUtil.hasKey(anyString())).thenReturn(false);

        // When
        userActivityInterceptor.preHandle(mockRequest, mockResponse, null);

        // Then
        verify(redisUtil).hasKey("user:activity:" + testUserId.toString());
        verify(redisUtil).set(eq("user:activity:" + testUserId.toString()), eq("1"), anyLong(), any());

        Thread.sleep(100); // wait for async
    }

    @Test
    void preHandle_whenValidUserButWithinInterval_shouldNotUpdateUserActivity() throws Exception {
        // Given
        mockRequest.setRequestURI("/api/test");
        mockRequest.setMethod("GET");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());

        when(redisUtil.hasKey(anyString())).thenReturn(true);

        // When
        userActivityInterceptor.preHandle(mockRequest, mockResponse, null);

        // Then
        verify(redisUtil).hasKey("user:activity:" + testUserId.toString());
        verifyNoMoreInteractions(redisUtil);
        verifyNoInteractions(userMapper);
    }

    @Test
    void preHandle_whenInvalidUserIdFormat_shouldNotUpdateUserActivity() throws Exception {
        // Given
        mockRequest.setRequestURI("/api/test");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn("invalid-uuid-format");

        // When
        userActivityInterceptor.preHandle(mockRequest, mockResponse, null);

        // Then
        verifyNoInteractions(userMapper);
        verifyNoInteractions(redisUtil);
    }

    @Test
    void shouldUpdateBasedOnInterval_whenRedisCheckFails_shouldReturnTrue() throws Exception {
        // Given
        when(redisUtil.hasKey(anyString())).thenThrow(new RuntimeException("Redis error"));

        // When
        boolean result = userActivityInterceptor.shouldUpdateBasedOnInterval(testUserId);

        // Then
        assertTrue(result);
    }

    @Test
    void updateUserLastActiveAsync_shouldUpdateUserLastActiveTime() throws InterruptedException {
        // Given
        when(userMapper.updateByPrimaryKeySelective(any(User.class))).thenReturn(1);

        // When
        userActivityInterceptor.updateUserLastActiveAsync(testUserId);

        Thread.sleep(100);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateByPrimaryKeySelective(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assert capturedUser.getUuid().equals(testUserId);
        assert capturedUser.getLastActive() != null;
    }

    @Test
    void updateUserLastActiveAsync_whenUpdateFails_shouldLogError() throws InterruptedException {
        // Given
        when(userMapper.updateByPrimaryKeySelective(any(User.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        userActivityInterceptor.updateUserLastActiveAsync(testUserId);

        Thread.sleep(100);

        // Then
        verify(userMapper).updateByPrimaryKeySelective(any(User.class));
    }
}
