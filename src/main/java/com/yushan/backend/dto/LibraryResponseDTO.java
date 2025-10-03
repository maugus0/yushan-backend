package com.yushan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryResponseDTO {
    private Integer id;
    private Integer novelId;
    private String novelTitle;
    private String novelAuthor;
    private String novelCover;
    private Integer progress;
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
