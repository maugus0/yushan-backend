package com.yushan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentStatisticsDTO {
    private Integer chapterId;
    private String chapterTitle;
    private long totalComments;
    private long spoilerComments;
    private long nonSpoilerComments;
    private Integer avgLikesPerComment;
    private Integer mostLikedCommentId;
}
