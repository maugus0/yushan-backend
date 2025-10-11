package com.yushan.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteUserResponseDTO {
    private Integer id;
    private Integer novelId;
    private String novelTitle;
    private LocalDateTime votedTime;
}
