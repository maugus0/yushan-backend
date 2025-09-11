package com.yushan.backend.entity;

import java.util.Date;

public class User {
    private String email;

    private String avatarUrl;

    private Integer status;

    private Boolean emailVerified;

    private Date createDate;

    private Date updateDate;

    private String uuid;

    private String username;

    private String hashPassword;

    private String profileDetail;

    private Boolean isAuthor;

    private Boolean authorVerified;

    private Float readTime;

    private Integer readBookNum;

    private Integer level;

    private Date birthday;

    private Integer gender;

    private Float point;

    private Integer exp;

    public User(String email, String avatarUrl, Integer status, Boolean emailVerified, Date createDate, Date updateDate, String uuid, String username, String hashPassword, String profileDetail, Boolean isAuthor, Boolean authorVerified, Float readTime, Integer readBookNum, Integer level, Date birthday, Integer gender, Float point, Integer exp) {
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.emailVerified = emailVerified;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.uuid = uuid;
        this.username = username;
        this.hashPassword = hashPassword;
        this.profileDetail = profileDetail;
        this.isAuthor = isAuthor;
        this.authorVerified = authorVerified;
        this.readTime = readTime;
        this.readBookNum = readBookNum;
        this.level = level;
        this.birthday = birthday;
        this.gender = gender;
        this.point = point;
        this.exp = exp;
    }

    public User() {
        super();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl == null ? null : avatarUrl.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
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

    public String getProfileDetail() {
        return profileDetail;
    }

    public void setProfileDetail(String profileDetail) {
        this.profileDetail = profileDetail == null ? null : profileDetail.trim();
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Float getPoint() {
        return point;
    }

    public void setPoint(Float point) {
        this.point = point;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }
}