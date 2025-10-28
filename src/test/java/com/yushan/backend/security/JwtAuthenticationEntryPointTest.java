package com.yushan.backend.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationEntryPoint Tests")
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @Mock
    private ServletOutputStream outputStream;

    @InjectMocks
    private JwtAuthenticationEntryPoint entryPoint;

    @Test
    @DisplayName("Test commence method")
    void testCommence() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getOutputStream()).thenReturn(outputStream);
        when(authException.getMessage()).thenReturn("Unauthorized");
        
        entryPoint.commence(request, response, authException);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    @DisplayName("Test commence with null exception")
    void testCommenceNullException() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getOutputStream()).thenReturn(outputStream);
        
        entryPoint.commence(request, response, null);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}

