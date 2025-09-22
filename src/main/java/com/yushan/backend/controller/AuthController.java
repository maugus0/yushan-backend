package com.yushan.backend.controller;

import com.yushan.backend.dto.UserRegisterationDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            response.put("success", true);
            response.put("message", "register successful");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "register failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            User user = authService.login(email, password);
            if (user != null) {
                //todo jwt & spring security
                session.setAttribute("USER_SESSION", user);
                session.setAttribute("USER_UUID", user.getUuid());
                response.put("success", true);
                response.put("message", "login successful");
                response.put("user", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "wrong email or password");
                return ResponseEntity.badRequest().body(response);
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                //todo jwt & spring security
                 request.getSession().invalidate();
            }
            response.put("success", true);
            response.put("message", "logout successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "logout failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
