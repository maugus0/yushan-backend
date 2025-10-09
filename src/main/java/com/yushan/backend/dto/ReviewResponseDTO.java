package com.yushan.backend.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ReviewResponseDTO {
    private Integer id;
    private UUID uuid;
    private UUID userId;
    private String username;
    private Integer novelId;
    private String novelTitle;
    private Integer rating;
    private String title;
    private String content;
    private Integer likeCnt;
    private Boolean isSpoiler;
    private Date createTime;
    private Date updateTime;

    public Date getCreateTime() {
        return createTime == null ? null : new Date(createTime.getTime());
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? null : new Date(createTime.getTime());
    }

    public Date getUpdateTime() {
        return updateTime == null ? null : new Date(updateTime.getTime());
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime == null ? null : new Date(updateTime.getTime());
    }
}
