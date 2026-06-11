package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TipLogResponseDto {

    private String tipLogId;
    private String tipId;
    private String tipName;
    private Integer scoreChange;
    private LocalDateTime appliedAt;

}