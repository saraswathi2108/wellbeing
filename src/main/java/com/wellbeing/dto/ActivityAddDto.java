package com.wellbeing.dto;

import com.wellbeing.entity.ActivityType;

import lombok.Data;

@Data
public class ActivityAddDto {
	
	private String activityName;
	private ActivityType activityType;
	private Integer contributionPercentage;

}
