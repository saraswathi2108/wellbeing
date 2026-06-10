package com.wellbeing.entity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
	
	@Id
	private String id;
	
	private String name;
	private String email;
	private String password;
	private Integer age;
	private String role;
	private String gender;

    @Enumerated(EnumType.STRING)
	private PrimaryRole primaryRole;

	private LocalDateTime wakeUpTime;
	private TimeZone timeZone;
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "user")
	private List<UserSubscription> userSubscriptions;
}
