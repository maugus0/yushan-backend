package com.yushan.backend.service;

import com.yushan.backend.dao.LibraryMapper;
import com.yushan.backend.dto.UserRegistrationRequestDTO;
import com.yushan.backend.dto.UserRegistrationResponseDTO;
import com.yushan.backend.entity.Library;
import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.enums.Gender;
import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LibraryMapper libraryMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EXPService expService;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    private static final Float DAILY_LOGIN_EXP = 5f;

    /**
     * register a new user
     * @param registrationDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public User register(UserRegistrationRequestDTO registrationDTO) {
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

        Gender gender = Gender.fromCode(registrationDTO.getGender());
        user.setGender(gender.getCode());
        user.setAvatarUrl(gender.getAvatarUrl());

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
        user.setStatus(1); // 1 for normal
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setLevel(1);
        user.setExp(0f);
        user.setYuan(0f);
        user.setReadTime(0f);
        user.setReadBookNum(0);

        userMapper.insert(user);

        // create user library
        Library library = new Library();
        library.setUuid(UUID.randomUUID());
        library.setUserId(user.getUuid());

        libraryMapper.insertSelective(library);
        return user;
    }

    /**
     * register a new user and create response
     * @param registrationDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public UserRegistrationResponseDTO registerAndCreateResponse(UserRegistrationRequestDTO registrationDTO) {

        User user = register(registrationDTO);

        // Generate JWT tokens for auto-login after registration
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        UserRegistrationResponseDTO responseDTO = createUserResponse(user);
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
        User user = userMapper.selectByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getHashPassword())) {
            return user;
        }
        else {
            throw new ValidationException("Invalid email or password");
        }
    }

    /**
     * login a user and create response
     * @param email
     * @param password
     * @return
     */
    public UserRegistrationResponseDTO loginAndCreateResponse(String email, String password) {
        User user = login(email, password);

        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // get last login time
        Date lastLogin = user.getLastLogin();

        // check if the first login in the day
        boolean isFirstLoginToday = false;
        if (lastLogin != null) {
            Calendar lastLoginCalendar = Calendar.getInstance();
            lastLoginCalendar.setTime(lastLogin);

            Calendar todayCalendar = Calendar.getInstance();

            isFirstLoginToday = lastLoginCalendar.get(Calendar.YEAR) != todayCalendar.get(Calendar.YEAR) ||
                    lastLoginCalendar.get(Calendar.DAY_OF_YEAR) != todayCalendar.get(Calendar.DAY_OF_YEAR);
        } else {
            isFirstLoginToday = true;
        }

        if(isFirstLoginToday) {
            // add exp
            Float newExp = user.getExp() + DAILY_LOGIN_EXP;
            user.setExp(newExp);
            user.setLevel(expService.checkLevel(newExp));
        }

        user.setLastLogin(new Date());
        user.setLastActive(new Date());

        // Prepare user info (without sensitive data)
        UserRegistrationResponseDTO responseDTO = createUserResponse(user);
        responseDTO.setAccessToken(accessToken);
        responseDTO.setRefreshToken(refreshToken);
        responseDTO.setTokenType("Bearer");
        responseDTO.setExpiresIn(accessTokenExpiration);
        responseDTO.setFirstLoginToday(isFirstLoginToday);

        userMapper.updateByPrimaryKeySelective(user);
        return responseDTO;
    }

    /**
     * refresh token and create response
     * @param refreshToken
     * @return
     */
    public UserRegistrationResponseDTO refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ValidationException("Invalid refresh token");
        }

        // Check if it's actually a refresh token
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new ValidationException("Token is not a refresh token");
        }

        // Extract user info from refresh token
        String email = jwtUtil.extractEmail(refreshToken);
        String userId = jwtUtil.extractUserId(refreshToken);

        // Load user from database
        User user = userMapper.selectByEmail(email);

        if (user == null || !user.getUuid().toString().equals(userId)) {
            throw new ValidationException("User not found or token mismatch");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user);

        // Optionally generate new refresh token (token rotation)
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        UserRegistrationResponseDTO responseDTO = createUserResponse(user);
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
    private UserRegistrationResponseDTO createUserResponse(User user) {
        UserRegistrationResponseDTO dto = new UserRegistrationResponseDTO();
        dto.setUuid(user.getUuid() != null ? user.getUuid().toString() : null);
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setProfileDetail(user.getProfileDetail());
        dto.setBirthday(user.getBirthday());
        dto.setGender(user.getGender());
        dto.setIsAuthor(user.getIsAuthor());
        dto.setIsAdmin(user.getIsAdmin());
        dto.setLevel(user.getLevel());
        dto.setExp(user.getExp());
        dto.setReadTime(user.getReadTime());
        dto.setReadBookNum(user.getReadBookNum());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        dto.setLastActive(user.getLastActive());
        return dto;
    }
}