package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * DTO for exposing user profile via API (read model)
 */
public class UserProfileResponseDTO {
    private String uuid;
    private String email;
    private String username;
    private String avatarUrl;
    private String profileDetail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date birthday;

    private Integer gender;
    private Boolean isAuthor;
    private Boolean authorVerified;
    private Integer level;
    private Float exp;
    private Float readTime;
    private Integer readBookNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private Date updateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private Date lastActive;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
        return birthday != null ? new Date(birthday.getTime()) : null;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday != null ? new Date(birthday.getTime()) : null;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Boolean getIsAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(Boolean isAuthor) {
        this.isAuthor = isAuthor;
    }

    public Boolean getAuthorVerified() {
        return authorVerified;
    }

    public void setAuthorVerified(Boolean authorVerified) {
        this.authorVerified = authorVerified;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Float getExp() {
        return exp;
    }

    public void setExp(Float exp) {
        this.exp = exp;
    }

    public Float getReadTime() {
        return readTime;
    }

    public void setReadTime(Float readTime) {
        this.readTime = readTime;
    }

    public Integer getReadBookNum() {
        return readBookNum;
    }

    public void setReadBookNum(Integer readBookNum) {
        this.readBookNum = readBookNum;
    }

    public Date getCreateTime() {
        return createTime != null ? new Date(createTime.getTime()) : null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
    }

    public Date getUpdateTime() {
        return updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public Date getLastActive() {
        return lastActive != null ? new Date(lastActive.getTime()) : null;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive != null ? new Date(lastActive.getTime()) : null;
    }
}


