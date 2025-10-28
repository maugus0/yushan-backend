package com.yushan.backend.security;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtFilter;

    @Test
    @DisplayName("Test shouldNotFilter - login path")
    void testShouldNotFilterLoginPath() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        
        boolean result = jwtFilter.shouldNotFilter(request);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test shouldNotFilter - register path")
    void testShouldNotFilterRegisterPath() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");
        
        boolean result = jwtFilter.shouldNotFilter(request);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test shouldNotFilter - refresh path")
    void testShouldNotFilterRefreshPath() {
        when(request.getRequestURI()).thenReturn("/api/auth/refresh");
        
        boolean result = jwtFilter.shouldNotFilter(request);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test shouldNotFilter - public path")
    void testShouldNotFilterPublicPath() {
        when(request.getRequestURI()).thenReturn("/api/public/test");
        
        boolean result = jwtFilter.shouldNotFilter(request);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test shouldNotFilter - OPTIONS method")
    void testShouldNotFilterOptionsMethod() {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("OPTIONS");
        
        boolean result = jwtFilter.shouldNotFilter(request);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test shouldNotFilter - normal path")
    void testShouldNotFilterNormalPath() {
        when(request.getRequestURI()).thenReturn("/api/novels");
        when(request.getMethod()).thenReturn("GET");
        
        boolean result = jwtFilter.shouldNotFilter(request);
        
        assertFalse(result);
    }

    @Test
    @DisplayName("Test doFilterInternal - valid token")
    void testDoFilterInternalValidToken() throws Exception {
        String token = "valid-token";
        String email = "test@example.com";
        
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername("testuser");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setStatus(1);
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(userMapper.selectByEmail(email)).thenReturn(user);
        when(jwtUtil.validateToken(token, user)).thenReturn(true);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test doFilterInternal - no token")
    void testDoFilterInternalNoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Test doFilterInternal - invalid token")
    void testDoFilterInternalInvalidToken() throws Exception {
        String token = "invalid-token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(false);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Test doFilterInternal - disabled user")
    void testDoFilterInternalDisabledUser() throws Exception {
        String token = "valid-token";
        String email = "test@example.com";
        
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername("testuser");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setStatus(2); // SUSPENDED
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(userMapper.selectByEmail(email)).thenReturn(user);
        when(jwtUtil.validateToken(token, user)).thenReturn(true);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Test doFilterInternal - user not found")
    void testDoFilterInternalUserNotFound() throws Exception {
        String token = "valid-token";
        String email = "test@example.com";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(userMapper.selectByEmail(email)).thenReturn(null);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Test doFilterInternal - already authenticated")
    void testDoFilterInternalAlreadyAuthenticated() throws Exception {
        String token = "valid-token";
        String email = "test@example.com";
        
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername("testuser");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setStatus(1);
        
        // Set up existing authentication
        CustomUserDetailsService.CustomUserDetails userDetails = 
            new CustomUserDetailsService.CustomUserDetails(user);
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth = 
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        // Should not call userMapper because already authenticated
        verify(userMapper, never()).selectByEmail(anyString());
        
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test doFilterInternal - exception handling")
    void testDoFilterInternalExceptionHandling() throws Exception {
        when(request.getHeader("Authorization")).thenThrow(new RuntimeException("Test exception"));
        
        assertDoesNotThrow(() -> {
            jwtFilter.doFilterInternal(request, response, filterChain);
        });
        
        verify(filterChain).doFilter(request, response);
    }
}

