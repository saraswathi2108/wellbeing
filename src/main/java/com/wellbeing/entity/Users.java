package com.wellbeing.entity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;


@Data
@Entity
public class Users {
	
	@Id
	private String id;
	
	private String name;
	private String email;
	private String password;
	private Integer age;
	private String role;
	private String gender;
	private String primaryRole;
	private LocalDateTime wakeUpTime;
	private TimeZone timeZone;
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "user")
	private List<UserSubscription> userSubscriptions;
}
