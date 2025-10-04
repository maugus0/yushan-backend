package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.dto.UserProfileUpdateRequestDTO;
import com.yushan.backend.dto.UserProfileUpdateResponseDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    /**
     * Load a user's profile by UUID and map to response DTO
     */
    public UserProfileResponseDTO getUserProfile(UUID userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return null;
        }
        return mapToProfileResponse(user);
    }

    /**
     * Update user profile selectively with allowed fields only
     */
    public UserProfileUpdateResponseDTO updateUserProfileSelective(UUID userId, UserProfileUpdateRequestDTO req) {
        User existing = userMapper.selectByPrimaryKey(userId);
        if (existing == null) {
            return null;
        }

        // Handle email change with verification
        boolean emailChanged = false;
        if (req.getEmail() != null && !req.getEmail().trim().isEmpty() && !req.getEmail().equals(existing.getEmail())) {
            // Check if verification code is provided
            if (req.getVerificationCode() == null || req.getVerificationCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Verification code is required for email change");
            }
            
            // Verify the email code
            boolean isValid = mailService.verifyEmail(req.getEmail(), req.getVerificationCode());
            if (!isValid) {
                throw new IllegalArgumentException("Invalid verification code or code expired");
            }
            
            // Check if new email already exists
            User userWithNewEmail = userMapper.selectByEmail(req.getEmail());
            if (userWithNewEmail != null) {
                throw new IllegalArgumentException("Email already exists");
            }
            
            // Only set emailChanged = true AFTER all validations pass
            emailChanged = true;
        }

        User toUpdate = new User();
        toUpdate.setUuid(userId);

        // Optional fields: update only if provided (non-null and non-empty)
        if (req.getUsername() != null && !req.getUsername().trim().isEmpty()) {
            toUpdate.setUsername(req.getUsername().trim());
        }
        if (req.getEmail() != null && !req.getEmail().trim().isEmpty() && !req.getEmail().equals(existing.getEmail())) {
            toUpdate.setEmail(req.getEmail().trim());
        }
        if (req.getGender() != null) {
            toUpdate.setGender(req.getGender());
            // check if update default gender URL
            if(Gender.isDefaultAvatar(existing.getAvatarUrl())){
                Gender gender = Gender.fromCode(req.getGender());
                toUpdate.setAvatarUrl(gender.getAvatarUrl());
            }
        }
        if (req.getAvatarUrl() != null && !req.getAvatarUrl().trim().isEmpty()) {
            toUpdate.setAvatarUrl(req.getAvatarUrl().trim());
        }
        if (req.getProfileDetail() != null && !req.getProfileDetail().trim().isEmpty()) {
            toUpdate.setProfileDetail(req.getProfileDetail().trim());
        }

        // update timestamp
        toUpdate.setUpdateTime(new Date());

        userMapper.updateByPrimaryKeySelective(toUpdate);

        // reload to get latest values
        User updated = userMapper.selectByPrimaryKey(userId);
        UserProfileResponseDTO profileResponse = mapToProfileResponse(updated);
        
        // Return response with email change flag
        return new UserProfileUpdateResponseDTO(profileResponse, emailChanged);
    }

    private UserProfileResponseDTO mapToProfileResponse(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
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

    /**
     * Send verification email for email change
     */
    public void sendEmailChangeVerification(String newEmail) {
        // Check if new email already exists
        User existingUser = userMapper.selectByEmail(newEmail);
        if (existingUser != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Send verification code to new email
        mailService.sendVerificationCode(newEmail);
    }
}

