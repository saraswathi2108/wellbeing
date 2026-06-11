package com.wellbeing.dto;

import java.time.LocalDateTime;

import com.wellbeing.entity.ActivityType;

import lombok.Data;

@Data
public class ActivityResponseDto {
	
	private String activityId;
	private String activityName;
	private ActivityType activityType;
	private Integer activityPercenage;
	private Boolean status;
	private LocalDateTime createdAt;

}
