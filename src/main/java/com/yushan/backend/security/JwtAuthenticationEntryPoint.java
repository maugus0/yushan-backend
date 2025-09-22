package com.yushan.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point
 * 
 * This class handles authentication failures and returns standardized 401 responses
 * when JWT authentication fails or is missing
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        // Set response headers
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Create error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Unauthorized: Invalid or missing JWT token");
        errorResponse.put("error", "UNAUTHORIZED");
        errorResponse.put("status", 401);
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        // Add more specific error message based on exception type
        if (authException != null) {
            String errorMessage = getErrorMessage(authException);
            errorResponse.put("details", errorMessage);
        }
        
        // Write response
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
    
    /**
     * Get specific error message based on authentication exception
     * 
     * @param authException Authentication exception
     * @return Specific error message
     */
    private String getErrorMessage(AuthenticationException authException) {
        String exceptionName = authException.getClass().getSimpleName();
        
        switch (exceptionName) {
            case "BadCredentialsException":
                return "Invalid credentials provided";
            case "AccountExpiredException":
                return "Account has expired";
            case "AccountLockedException":
                return "Account is locked";
            case "DisabledException":
                return "Account is disabled";
            case "CredentialsExpiredException":
                return "Credentials have expired";
            case "InsufficientAuthenticationException":
                return "Insufficient authentication information";
            case "AuthenticationCredentialsNotFoundException":
                return "Authentication credentials not found";
            case "JwtException":
                return "JWT token is invalid or malformed";
            case "ExpiredJwtException":
                return "JWT token has expired";
            case "UnsupportedJwtException":
                return "JWT token is unsupported";
            case "MalformedJwtException":
                return "JWT token is malformed";
            case "SignatureException":
                return "JWT signature verification failed";
            case "IllegalArgumentException":
                return "JWT token compact of handler are invalid";
            default:
                return "Authentication failed: " + authException.getMessage();
        }
    }
}
