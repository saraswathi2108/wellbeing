package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserMonthlyStatsDto {
    private Double averageWellbeingScore;
    private String mostRecoveredActivity;
    private String mostDrainedActivity;
}