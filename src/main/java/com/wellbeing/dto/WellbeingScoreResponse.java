package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WellbeingScoreResponse {

    private String userId;
    private String wellScoreId;
    private Integer currentScore;
    private LocalDateTime updatedAt;

}