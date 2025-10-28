package com.yushan.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Exception classes
 */
@DisplayName("Exception Tests")
class ExceptionTest {

    @Test
    @DisplayName("Test ResourceNotFoundException")
    void testResourceNotFoundException() {
        // Test with message
        ResourceNotFoundException exception1 = new ResourceNotFoundException("User not found");
        assertEquals("User not found", exception1.getMessage());
        
        // Test with message and cause
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException exception2 = new ResourceNotFoundException("Novel not found", cause);
        assertEquals("Novel not found", exception2.getMessage());
        assertEquals(cause, exception2.getCause());
    }

    @Test
    @DisplayName("Test UnauthorizedException")
    void testUnauthorizedException() {
        // Test with message
        UnauthorizedException exception1 = new UnauthorizedException("Authentication required");
        assertEquals("Authentication required", exception1.getMessage());
        
        // Test with message and cause
        Throwable cause = new RuntimeException("Root cause");
        UnauthorizedException exception2 = new UnauthorizedException("Token expired", cause);
        assertEquals("Token expired", exception2.getMessage());
        assertEquals(cause, exception2.getCause());
    }

    @Test
    @DisplayName("Test ForbiddenException")
    void testForbiddenException() {
        // Test with message
        ForbiddenException exception1 = new ForbiddenException("Access denied");
        assertEquals("Access denied", exception1.getMessage());
        
        // Test with message and cause
        Throwable cause = new RuntimeException("Root cause");
        ForbiddenException exception2 = new ForbiddenException("Insufficient permissions", cause);
        assertEquals("Insufficient permissions", exception2.getMessage());
        assertEquals(cause, exception2.getCause());
    }

    @Test
    @DisplayName("Test ValidationException")
    void testValidationException() {
        // Test with message
        ValidationException exception1 = new ValidationException("Invalid input");
        assertEquals("Invalid input", exception1.getMessage());
        
        // Test with message and cause
        Throwable cause = new RuntimeException("Root cause");
        ValidationException exception2 = new ValidationException("Email already exists", cause);
        assertEquals("Email already exists", exception2.getMessage());
        assertEquals(cause, exception2.getCause());
    }
}

