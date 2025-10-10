package com.yushan.backend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class HistoryResponseDTO {
    private Integer historyId;
    private Integer novelId;
    private String novelTitle;
    private String novelCover;
    private Integer categoryId;
    private String categoryName;
    private Float avgRating;
    private String synopsis;
    private Integer chapterId;
    private Integer chapterNumber;
    private Integer chapterCnt;
    private boolean isInLibrary;
    private Date viewTime;
    
    // Safe getter that returns a copy to avoid exposing internal representation
    public Date getViewTime() {
        return viewTime != null ? new Date(viewTime.getTime()) : null;
    }
    
    // Safe setter that stores a copy to avoid exposing internal representation
    public void setViewTime(Date viewTime) {
        this.viewTime = viewTime != null ? new Date(viewTime.getTime()) : null;
    }
}