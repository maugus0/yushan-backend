package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileUpdateResponseDTO {
    private UserProfileResponseDTO profile;
    private boolean emailChanged;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    
    public UserProfileUpdateResponseDTO(UserProfileResponseDTO profile, boolean emailChanged) {
        this.profile = profile != null ? createCopy(profile) : null;
        this.emailChanged = emailChanged;
    }
    
    public UserProfileUpdateResponseDTO(UserProfileResponseDTO profile, boolean emailChanged, 
                                      String accessToken, String refreshToken, String tokenType, Long expiresIn) {
        this.profile = profile != null ? createCopy(profile) : null;
        this.emailChanged = emailChanged;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }
    
    public UserProfileResponseDTO getProfile() {
        return profile != null ? createCopy(profile) : null;
    }
    
    public void setProfile(UserProfileResponseDTO profile) {
        this.profile = profile != null ? createCopy(profile) : null;
    }
    
    private UserProfileResponseDTO createCopy(UserProfileResponseDTO original) {
        UserProfileResponseDTO copy = new UserProfileResponseDTO();
        copy.setUuid(original.getUuid());
        copy.setEmail(original.getEmail());
        copy.setUsername(original.getUsername());
        copy.setAvatarUrl(original.getAvatarUrl());
        copy.setProfileDetail(original.getProfileDetail());
        copy.setBirthday(original.getBirthday());
        copy.setGender(original.getGender());
        copy.setIsAuthor(original.getIsAuthor());
        copy.setLevel(original.getLevel());
        copy.setExp(original.getExp());
        copy.setReadTime(original.getReadTime());
        copy.setReadBookNum(original.getReadBookNum());
        copy.setCreateTime(original.getCreateTime());
        copy.setUpdateTime(original.getUpdateTime());
        copy.setLastActive(original.getLastActive());
        return copy;
    }
}
