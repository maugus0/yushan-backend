package com.yushan.backend.controller;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.User;
import com.yushan.backend.service.AuthService;
import com.yushan.backend.service.MailService;
import com.yushan.backend.common.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserMapper userMapper;


    @GetMapping("/test")
    public String test() {
        log.info("test");
        return "test";
    }

    /**
     * verifyEmail & Register a new user
     * @param registrationDTO
     * @return
     */
    @PostMapping("/register")
    public Result<UserRegisterationResponseDTO> register(@Valid @RequestBody UserRegisterationRequestDTO registrationDTO) {
        try {
            // no need to check if email exists here since we check it in register()
            boolean isValid = mailService.verifyEmail(registrationDTO.getEmail(), registrationDTO.getCode());

            if (!isValid) {
                return Result.error("Invalid verification code or code expired");
            }

            // Prepare user info & token (without sensitive data)
            UserRegisterationResponseDTO responseDTO = authService.registerAndCreateResponse(registrationDTO);

            return Result.success(responseDTO);
        } catch (Exception e) {
            return Result.error("register failed: " + e.getMessage());
        }
    }

    /**
     * Login a user
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public Result<UserRegisterationResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            UserRegisterationResponseDTO responseDTO = authService.loginAndCreateResponse(email, password);
            if(responseDTO == null) {
                return Result.error("Invalid email or password");
            } else {
                return Result.success(responseDTO);
            }
        } catch (Exception e) {
            return Result.error("login failed: " + e.getMessage());
        }
    }

    /**
     * Logout a user
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        try {
            // Clear SecurityContext
            SecurityContextHolder.clearContext();

            return Result.success("JWT tokens are stateless and cannot be invalidated server-side. Client should discard tokens.");
        } catch (Exception e) {
            return Result.error("logout failed: " + e.getMessage());
        }
    }

    /**
     * Refresh a user's access token
     * @param refreshRequest
     * @return
     */
    @PostMapping("/refresh")
    public Result<UserRegisterationResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO refreshRequest) {
        try {
            String refreshToken = refreshRequest.getRefreshToken();

            UserRegisterationResponseDTO responseDTO = authService.refreshToken(refreshToken);
            return Result.success(responseDTO);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("Refresh failed: " + e.getMessage());
        }
    }

    /**
     * (re)Send verification email to a user
     * @param emailRequest
     * @return
     */
    @PostMapping("/send-email")
    public Result<String> sendEmail(@RequestBody EmailVerificationRequestDTO emailRequest) {
        try {
            String email = emailRequest.getEmail();

            // check here instead of dto since only one field
            if (email == null || email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return Result.error("wrong email");
            }

            //query email if exists
            User user = userMapper.selectByEmail(email);
            if (user != null) {
                return Result.error("email exists");
            }

            mailService.sendVerificationCode(email);

            return Result.success("Verification code sent successfully");
        } catch (Exception e) {
            return Result.error("Failed to send email: " + e.getMessage());
        }
    }
}
