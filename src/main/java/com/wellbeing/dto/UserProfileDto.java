package com.wellbeing.dto;

import java.time.LocalDateTime;
import java.util.TimeZone;

import com.wellbeing.entity.PrimaryRole;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
	
	private String userId;
	private String name;
	private String email;
	private Integer age;
	private String gender;

    @Enumerated(EnumType.STRING)
	private PrimaryRole primaryRole;

	private LocalDateTime wakeUpTime;
	private TimeZone timeZone;
	

}
