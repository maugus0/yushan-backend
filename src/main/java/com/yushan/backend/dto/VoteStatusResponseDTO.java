package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class VoteStatusResponseDTO {
    private Integer novelId;
    private Boolean hasVoted;
    private Date votedAt;

    public VoteStatusResponseDTO(Integer novelId, Boolean hasVoted, Date votedAt) {
        this.novelId = novelId;
        this.hasVoted = hasVoted;
        this.votedAt = votedAt != null ? new Date(votedAt.getTime()) : null;
    }

    public Date getVotedAt() {
        return votedAt != null ? new Date(votedAt.getTime()) : null;
    }

    public void setVotedAt(Date votedAt) {
        this.votedAt = votedAt != null ? new Date(votedAt.getTime()) : null;
    }
}
