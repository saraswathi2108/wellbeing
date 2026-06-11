package com.wellbeing.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityLogResponseDto {
    private String logId;
    private String activityName;
    private String activityType;
    private Integer scoreChange;
    private LocalDateTime completedAt;
}