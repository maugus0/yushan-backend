package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * DTO for user profile update requests (write model)
 */
public class UserProfileUpdateRequestDTO {

    @Size(min = 3, max = 20, message = "username must be 3-20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "username may contain letters, digits, ., _, -")
    private String username;

    @Size(max = 512, message = "avatarUrl must be at most 512 characters")
    private String avatarUrl;

    @Size(max = 1000, message = "profileDetail must be at most 1000 characters")
    private String profileDetail;

    @Past(message = "birthday must be a past date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date birthday;

    @Pattern(regexp = "^(0|1|2)$", message = "gender must be 0, 1 or 2")
    private String gender;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getProfileDetail() {
        return profileDetail;
    }

    public void setProfileDetail(String profileDetail) {
        this.profileDetail = profileDetail;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}


