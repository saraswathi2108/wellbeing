package com.wellbeing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanUsersResponseDto {
	
	private String userId;
	private String name;
	private String email;
	private String phoneNo;
	private String role;
	private String gender;

}
