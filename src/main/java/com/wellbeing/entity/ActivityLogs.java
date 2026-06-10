package com.wellbeing.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ActivityLogs {
	
	
	@Id
	private String id;
	
	@ManyToOne
	@JoinColumn(name = "activity_id", referencedColumnName = "id")
	private Activities activity;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Users user;
	
	private LocalDateTime createdAt;
	
	private Integer scoreChange;
	

}
