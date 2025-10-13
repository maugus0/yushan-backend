package com.yushan.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovelRankDTO {
    private Integer novelId;
    private Integer rank;
    private Double score;
    private String rankType;

    public NovelRankDTO(Integer novelId, long l, Double score, String rankType) {
        this.novelId = novelId;
        this.rank = (int) l;
        this.score = score;
        this.rankType = rankType;
    }
}