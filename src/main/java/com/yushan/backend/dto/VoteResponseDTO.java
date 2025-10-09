package com.yushan.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponseDTO {
    private Integer novelId;
    private Integer voteCount;
    private Boolean userVoted;
}
