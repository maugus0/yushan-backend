package com.yushan.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteUserResponseDTO {
    private Integer id;
    private Integer novelId;
    private String novelTitle;
    private Date votedTime;

    public static Object builder() {
        return null;
    }

    public Date getVotedTime() {
        return votedTime != null ? new Date(votedTime.getTime()) : null;
    }

    public void setVotedTime(Date votedAt) {
        this.votedTime = votedTime != null ? new Date(votedTime.getTime()) : null;
    }
}
