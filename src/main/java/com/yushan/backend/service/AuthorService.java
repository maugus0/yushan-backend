package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthorService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    /**
     * Upgrade user to author with verification code
     */
    public UserProfileResponseDTO upgradeToAuthor(String email, String verificationCode) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        // Get user from database
        User user = userMapper.selectByEmail(email.trim().toLowerCase(java.util.Locale.ROOT));
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        if (user.getIsAuthor() != null && user.getIsAuthor()) {
            throw new IllegalArgumentException("User is already an author");
        }

        // Verify code using existing method
        boolean isValid = mailService.verifyEmail(email, verificationCode);
        if (!isValid) {
            throw new IllegalArgumentException("Invalid verification code or code expired");
        }

        // Update user to author
        user.setIsAuthor(true);
        user.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(user);
        
        // Return updated user profile
        return userService.getUserProfile(user.getUuid());
    }
}
