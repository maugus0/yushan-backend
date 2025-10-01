package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExampleResponseDTO {
    private String message;
    private String access;
    private String user;
    private Boolean isAuthor;
    private Boolean isVerifiedAuthor;
    private Boolean isAdmin;
    private String userId;
    private String currentUserId;
    private Boolean isOwner;
    private String resourceId;
    private List<String> authorities;
    
    // Defensive copying for authorities field
    public List<String> getAuthorities() {
        return authorities != null ? new java.util.ArrayList<>(authorities) : null;
    }
    
    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities != null ? new java.util.ArrayList<>(authorities) : null;
    }
    
    // Constructor for basic responses
    public ExampleResponseDTO(String message, String access) {
        this.message = message;
        this.access = access;
    }
    
    // Constructor for user-specific responses
    public ExampleResponseDTO(String message, String access, String user, Boolean isAuthor) {
        this.message = message;
        this.access = access;
        this.user = user;
        this.isAuthor = isAuthor;
    }
    
    // Full constructor with defensive copying for authorities
    public ExampleResponseDTO(String message, String access, String user, Boolean isAuthor, 
                             Boolean isVerifiedAuthor, Boolean isAdmin, String userId, 
                             String currentUserId, Boolean isOwner, String resourceId, 
                             List<String> authorities) {
        this.message = message;
        this.access = access;
        this.user = user;
        this.isAuthor = isAuthor;
        this.isVerifiedAuthor = isVerifiedAuthor;
        this.isAdmin = isAdmin;
        this.userId = userId;
        this.currentUserId = currentUserId;
        this.isOwner = isOwner;
        this.resourceId = resourceId;
        this.authorities = authorities != null ? new java.util.ArrayList<>(authorities) : null;
    }
}
