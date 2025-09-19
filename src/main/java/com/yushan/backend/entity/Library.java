package com.yushan.backend.entity;

import java.util.Date;
import java.util.UUID;

public class Library {
    private Integer id;

    private UUID uuid;

    private UUID userId;

    private Date createTime;

    private Date updateTime;

    public Library(Integer id, UUID uuid, UUID userId, Date createTime, Date updateTime) {
        this.id = id;
        this.uuid = uuid;
        this.userId = userId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Library() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}