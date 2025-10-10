package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class LibraryResponseDTO {
    private Integer id;
    private Integer novelId;
    private String novelTitle;
    private String novelAuthor;
    private String novelCover;
    //chapter_id
    private Integer progress;
    private Integer chapterNumber;
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

    public LibraryResponseDTO(Integer id, Integer novelId, String novelTitle, String novelAuthor, String novelCover, Integer progress, Date createTime, Date updateTime) {
        this.id = id;
        this.novelId = novelId;
        this.novelTitle = novelTitle;
        this.novelAuthor = novelAuthor;
        this.novelCover = novelCover;
        this.progress = progress;
        this.createTime = createTime == null ? null : new Date(createTime.getTime());
        this.updateTime = updateTime == null ? null : new Date(updateTime.getTime());
    }
}
