package com.yushan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentModerationStatsDTO {
    private Long totalComments;
    private Long spoilerComments;
    private Long nonSpoilerComments;
    private Long commentsToday;
    private Long commentsThisWeek;
    private Long commentsThisMonth;
    private Double avgCommentsPerChapter;
    private Double avgCommentsPerUser;
    private Integer mostActiveUserId;
    private String mostActiveUsername;
    private Long mostActiveUserCommentCount;
    private Integer mostCommentedChapterId;
    private String mostCommentedChapterTitle;
    private Long mostCommentedChapterCount;
}