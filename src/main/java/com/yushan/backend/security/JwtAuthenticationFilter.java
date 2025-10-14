package com.yushan.backend.security;

import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * This filter runs before every request and:
 * 1. Extracts JWT token from Authorization header
 * 2. Validates the token
 * 3. Extracts email from token
 * 4. Loads user from database
 * 5. Sets authentication in SecurityContext
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    /**
     * Filter method that processes each request
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. Extract token from Authorization header
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                // 2. Extract email from token
                String email = jwtUtil.extractEmail(token);
                
                // 3. Check if user is not already authenticated
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 4. Load user from database
                    User user = userMapper.selectByEmail(email);
                    
                    if (user != null && jwtUtil.validateToken(token, user)) {
                        // 5. Create CustomUserDetails from User
                        CustomUserDetailsService.CustomUserDetails userDetails = 
                            new CustomUserDetailsService.CustomUserDetails(user);
                        
                        // 5.5. Check if user is enabled (not suspended/banned)
                        if (!userDetails.isEnabled()) {
                            // User is disabled, don't authenticate
                            filterChain.doFilter(request, response);
                            return;
                        }
                        
                        // 6. Create authentication object
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        // 7. Set additional details
                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        
                        // 8. Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't stop the request
            logger.error("Cannot set user authentication: " + e.getMessage(), e);
        }
        
        // 8. Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * 
     * @param request HTTP request
     * @return JWT token or null if not found
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        
        return null;
    }

    /**
     * Check if the request should be filtered
     * Skip filtering for certain paths (like login, register)
     * 
     * @param request HTTP request
     * @return true if should skip filtering, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip JWT filtering for these paths
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/api/auth/refresh") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/actuator/") ||
               path.equals("/error") ||
               // Skip OPTIONS requests (CORS preflight)
               "OPTIONS".equals(method);
    }
}
