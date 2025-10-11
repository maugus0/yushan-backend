package com.yushan.backend.dto;

import lombok.Data;

@Data
public class VoteResponseDTO {
    private Integer novelId;
    private Integer voteCount;
    private Float remainedYuan;

    public VoteResponseDTO(Integer novelId, Integer updatedVoteCount, Float remainedYuan) {
        this.novelId = novelId;
        this.voteCount = updatedVoteCount;
        this.remainedYuan = remainedYuan;
    }
}
