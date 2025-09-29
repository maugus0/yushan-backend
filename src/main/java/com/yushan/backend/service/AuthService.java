package com.yushan.backend.service;

import com.yushan.backend.dto.UserRegisterationRequestDTO;
import com.yushan.backend.dto.UserRegisterationResponseDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    /**
     * register a new user
     * @param registrationDTO
     * @return
     */
    public User register(UserRegisterationRequestDTO registrationDTO) {
        // check if email existed
        if (userMapper.selectByEmail(registrationDTO.getEmail()) != null) {
            throw new RuntimeException("email was registered");
        }

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(registrationDTO.getEmail());
        user.setUsername(registrationDTO.getUsername());
        user.setHashPassword(hashPassword(registrationDTO.getPassword()));
        user.setEmailVerified(true);

        if (registrationDTO.getGender() != null) {
            user.setGender(registrationDTO.getGender());
        } else {
            user.setGender(0); // default for unknown gender
        }
        if (registrationDTO.getBirthday() != null) {
            user.setBirthday(registrationDTO.getBirthday());
        } else {
            user.setBirthday(null);
        }

        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLogin(new Date());
        user.setLastActive(new Date());

        // set default user profile
        user.setAvatarUrl("123"); //todo: set default avatar URL
        user.setStatus(1); // 1 for normal
        user.setIsAuthor(false);
        user.setAuthorVerified(false);
        user.setLevel(1);
        user.setExp(0f);
        user.setYuan(0f);
        user.setReadTime(0f);
        user.setReadBookNum(0);

        try {
            userMapper.insert(user);
        } catch (Exception e) {
            userMapper.deleteByPrimaryKey(user.getUuid());
            throw new RuntimeException("registered failed", e);
        }
        return user;
    }

    /**
     * register a new user and create response
     * @param registrationDTO
     * @return
     */
    public UserRegisterationResponseDTO registerAndCreateResponse(UserRegisterationRequestDTO registrationDTO) {

        User user = register(registrationDTO);

        // Generate JWT tokens for auto-login after registration
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        UserRegisterationResponseDTO responseDTO = new UserRegisterationResponseDTO();
        responseDTO.setUser(createUserResponse(user));
        responseDTO.setAccessToken(accessToken);
        responseDTO.setRefreshToken(refreshToken);
        responseDTO.setTokenType("Bearer");
        responseDTO.setExpiresIn(accessTokenExpiration);
        return responseDTO;
    }

    /**
     * login a user
     * @param email
     * @param password
     * @return
     */
    public User login(String email, String password) {
        // verify input
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        try {
            User user = userMapper.selectByEmail(email);
            if (user != null && BCrypt.checkpw(password, user.getHashPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * login a user and create response
     * @param email
     * @param password
     * @return
     */
    public UserRegisterationResponseDTO loginAndCreateResponse(String email, String password) {
        User user = login(email, password);
        UserRegisterationResponseDTO responseDTO = new UserRegisterationResponseDTO();

        if (user != null) {
            // Generate JWT tokens
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            // Prepare user info (without sensitive data)
            responseDTO.setUser(createUserResponse(user));
            responseDTO.setAccessToken(accessToken);
            responseDTO.setRefreshToken(refreshToken);
            responseDTO.setTokenType("Bearer");
            responseDTO.setExpiresIn(accessTokenExpiration);
            return responseDTO;
        }
        return null;
    }

    /**
     * refresh token and create response
     * @param refreshToken
     * @return
     */
    public UserRegisterationResponseDTO refreshToken(String refreshToken) {
        UserRegisterationResponseDTO responseDTO = new UserRegisterationResponseDTO();
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Check if it's actually a refresh token
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Token is not a refresh token");
        }

        // Extract user info from refresh token
        String email = jwtUtil.extractEmail(refreshToken);
        String userId = jwtUtil.extractUserId(refreshToken);

        // Load user from database
        User user = userMapper.selectByEmail(email);

        if (user == null || !user.getUuid().toString().equals(userId)) {
            throw new IllegalArgumentException("User not found or token mismatch");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user);

        // Optionally generate new refresh token (token rotation)
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        responseDTO.setUser(createUserResponse(user));
        responseDTO.setAccessToken(newAccessToken);
        responseDTO.setRefreshToken(newRefreshToken);
        responseDTO.setTokenType("Bearer");
        responseDTO.setExpiresIn(accessTokenExpiration);
        return responseDTO;
    }

    /**
     * hash password
     * @param password
     * @return
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * create user response (without sensitive data)
     * @param user
     * @return
     */
    private User createUserResponse(User user) {
        User userInfo = new User();
        userInfo.setUuid(user.getUuid());
        userInfo.setEmail(user.getEmail());
        userInfo.setUsername(user.getUsername());
        userInfo.setIsAuthor(user.getIsAuthor());
        userInfo.setAuthorVerified(user.getAuthorVerified());
        userInfo.setLevel(user.getLevel());
        userInfo.setExp(user.getExp());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setStatus(user.getStatus());
        userInfo.setCreateTime(user.getCreateTime());
        userInfo.setUpdateTime(user.getUpdateTime());
        userInfo.setLastActive(user.getLastActive());
        userInfo.setLastLogin(user.getLastLogin());
        return userInfo;
    }
}