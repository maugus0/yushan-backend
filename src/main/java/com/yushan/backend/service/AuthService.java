package com.yushan.backend.service;

import com.yushan.backend.dto.UserRegisterationDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    public User register(UserRegisterationDTO registrationDTO) {
        // check if email existed
        if (userMapper.selectByEmail(registrationDTO.getEmail()) != null) {
            throw new RuntimeException("email was registered");
        }

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(registrationDTO.getEmail());
        user.setUsername(registrationDTO.getUsername());
        user.setHashPassword(hashPassword(registrationDTO.getPassword()));
        user.setEmailVerified(false);
        user.setAvatarUrl(""); // Set default empty string
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setStatus(1); // 1 for normal user

        // set default user profile
        user.setIsAuthor(false);
        user.setAuthorVerified(false);
        user.setLevel(1);
        user.setExp(0f);
        user.setYuan(0f);
        user.setReadTime(0f);
        user.setReadBookNum(0);
        user.setLastLogin(new Date());
        user.setLastActive(new Date());

        try {
            userMapper.insert(user);
        } catch (Exception e) {
            userMapper.deleteByPrimaryKey(user.getUuid());
            throw new RuntimeException("registered failed", e);
        }
        verifyEmail(user.getEmail());
        return user;
    }

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

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private void verifyEmail(String email) {
    }
}
