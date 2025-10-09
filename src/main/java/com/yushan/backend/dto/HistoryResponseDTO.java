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
}