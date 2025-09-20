package com.yushan.backend.controller;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserRegisterationDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.service.AuthService;
import com.yushan.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @GetMapping("/test")
    public String test() {
        log.info("test");
        return "test";
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRegisterationDTO registrationDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = authService.register(registrationDTO);
            
            // Generate JWT tokens for auto-login after registration
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            
            // Prepare user info (without sensitive data)
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("uuid", user.getUuid().toString());
            userInfo.put("email", user.getEmail());
            userInfo.put("username", user.getUsername());
            userInfo.put("isAuthor", user.getIsAuthor());
            userInfo.put("authorVerified", user.getAuthorVerified());
            userInfo.put("level", user.getLevel());
            userInfo.put("exp", user.getExp());
            
            response.put("success", true);
            response.put("message", "register successful");
            response.put("user", userInfo);
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", accessTokenExpiration);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "register failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            User user = authService.login(email, password);
            if (user != null) {
                // Generate JWT tokens
                String accessToken = jwtUtil.generateAccessToken(user);
                String refreshToken = jwtUtil.generateRefreshToken(user);
                
                // Prepare user info (without sensitive data)
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("uuid", user.getUuid().toString());
                userInfo.put("email", user.getEmail());
                userInfo.put("username", user.getUsername());
                userInfo.put("isAuthor", user.getIsAuthor());
                userInfo.put("authorVerified", user.getAuthorVerified());
                userInfo.put("level", user.getLevel());
                userInfo.put("exp", user.getExp());
                
                response.put("success", true);
                response.put("message", "login successful");
                response.put("user", userInfo);
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                response.put("tokenType", "Bearer");
                response.put("expiresIn", accessTokenExpiration);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Clear SecurityContext
            SecurityContextHolder.clearContext();
            
            response.put("success", true);
            response.put("message", "logout successful");
            response.put("note", "JWT tokens are stateless and cannot be invalidated server-side. Client should discard tokens.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "logout failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, String> refreshRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String refreshToken = refreshRequest.get("refreshToken");
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                response.put("success", false);
                response.put("message", "Refresh token is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                response.put("success", false);
                response.put("message", "Invalid refresh token");
                return ResponseEntity.status(401).body(response);
            }
            
            // Check if it's actually a refresh token
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                response.put("success", false);
                response.put("message", "Token is not a refresh token");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Extract user info from refresh token
            String email = jwtUtil.extractEmail(refreshToken);
            String userId = jwtUtil.extractUserId(refreshToken);
            
            // Load user from database
            User user = userMapper.selectByEmail(email);
            
            if (user == null || !user.getUuid().toString().equals(userId)) {
                response.put("success", false);
                response.put("message", "User not found or token mismatch");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generate new access token
            String newAccessToken = jwtUtil.generateAccessToken(user);
            
            // Optionally generate new refresh token (token rotation)
            String newRefreshToken = jwtUtil.generateRefreshToken(user);
            
            // Prepare user info
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("uuid", user.getUuid().toString());
            userInfo.put("email", user.getEmail());
            userInfo.put("username", user.getUsername());
            userInfo.put("isAuthor", user.getIsAuthor());
            userInfo.put("authorVerified", user.getAuthorVerified());
            userInfo.put("level", user.getLevel());
            userInfo.put("exp", user.getExp());
            
            response.put("success", true);
            response.put("message", "Token refreshed successfully");
            response.put("user", userInfo);
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", newRefreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", accessTokenExpiration);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Refresh failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
