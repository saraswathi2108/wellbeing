package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MostUsedActivitiesDto {
    private String mostUsedDrain;
    private String mostUsedRecovery;
}