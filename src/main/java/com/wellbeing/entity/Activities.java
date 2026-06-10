package com.wellbeing.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Activities {
	
	@Id
	private String id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Users user;
	
	private String activityName;
	
	private ActivityType activityType;
	private String activityPercentage;
	private Boolean status;
	private LocalDateTime createdAt;
	
	

}
