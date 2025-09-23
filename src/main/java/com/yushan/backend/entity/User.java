package com.yushan.backend.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID uuid;

    private String email;

    private String username;

    private String hashPassword;

    private Boolean emailVerified;

    private String avatarUrl;

    private String profileDetail;

    private Date birthday;

    private Integer gender;

    private Integer status;

    private Boolean isAuthor;

    private Boolean authorVerified;

    private Integer level;

    private Float exp;

    private Float yuan;

    private Float readTime;

    private Integer readBookNum;

    private Date createTime;

    private Date updateTime;

    private Date lastLogin;

    private Date lastActive;

    public User(UUID uuid, String email, String username, String hashPassword, Boolean emailVerified, String avatarUrl, String profileDetail, Date birthday, Integer gender, Integer status, Boolean isAuthor, Boolean authorVerified, Integer level, Float exp, Float yuan, Float readTime, Integer readBookNum, Date createTime, Date updateTime, Date lastLogin, Date lastActive) {
        this.uuid = uuid;
        this.email = email;
        this.username = username;
        this.hashPassword = hashPassword;
        this.emailVerified = emailVerified;
        this.avatarUrl = avatarUrl;
        this.profileDetail = profileDetail;
        this.birthday = birthday != null ? new Date(birthday.getTime()) : null;
        this.gender = gender;
        this.status = status;
        this.isAuthor = isAuthor;
        this.authorVerified = authorVerified;
        this.level = level;
        this.exp = exp;
        this.yuan = yuan;
        this.readTime = readTime;
        this.readBookNum = readBookNum;
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
        this.lastLogin = lastLogin != null ? new Date(lastLogin.getTime()) : null;
        this.lastActive = lastActive != null ? new Date(lastActive.getTime()) : null;
    }

    public User() {
        super();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword == null ? null : hashPassword.trim();
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl == null ? null : avatarUrl.trim();
    }

    public String getProfileDetail() {
        return profileDetail;
    }

    public void setProfileDetail(String profileDetail) {
        this.profileDetail = profileDetail == null ? null : profileDetail.trim();
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Float getYuan() {
        return yuan;
    }

    public void setYuan(Float yuan) {
        this.yuan = yuan;
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

    public Date getLastLogin() {
        return lastLogin != null ? new Date(lastLogin.getTime()) : null;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin != null ? new Date(lastLogin.getTime()) : null;
    }

    public Date getLastActive() {
        return lastActive != null ? new Date(lastActive.getTime()) : null;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive != null ? new Date(lastActive.getTime()) : null;
    }
}