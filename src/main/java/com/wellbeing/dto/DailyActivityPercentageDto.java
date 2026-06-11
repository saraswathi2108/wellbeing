package com.wellbeing.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyActivityPercentageDto {
    private LocalDate date;
    private Integer totalPercentage; 
}