package com.wellbeing.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wellbeing.dto.ActivityAddDto;
import com.wellbeing.dto.ActivityLogResponseDto;
import com.wellbeing.dto.ActivityResponseDto;
import com.wellbeing.dto.DailyActivityPercentageDto;
import com.wellbeing.dto.UserProfileDto;
import com.wellbeing.entity.ActivityType;
import com.wellbeing.service.CustomUserDetails;
import com.wellbeing.service.UserService;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	
	@PostMapping("/addActivity")
	public String addActivity(@RequestBody ActivityAddDto activityAddDto,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		
		String userId = currentUser.getId();
		log.info("Current logged in user id: {}", userId);
		
		return userService.addActivity(activityAddDto, userId);
	}
	
	
	@PostMapping("/completeActivity")
	public String completedActivity(@RequestParam String activityId,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		
		String userId = currentUser.getId();
		return userService.logCompletedActivity(activityId, userId);
	}
	
	
	
	@GetMapping("/getActivities")
	public List<ActivityResponseDto> getActivities(@RequestParam (required = false) ActivityType activityType,
									@AuthenticationPrincipal CustomUserDetails currentUser){
		
		String userId = currentUser.getId();
		
		return userService.getActivities(activityType, userId);
	}
	
	
	
	@PutMapping("/delete/{activityId}")
	public String deleteActivity(@PathVariable String activityId,
								@RequestParam Boolean status) {
		
		return userService.deleteActivity(activityId, status);
	}
	
	
	
	@GetMapping("/recent-activities")
	public List<ActivityLogResponseDto> getRecentActivities(@AuthenticationPrincipal CustomUserDetails currentUser) {
		
		String userId = currentUser.getId();
		
		return userService.getRecentActivities(userId);
	}
	
	
	@GetMapping("/activities/last-7-days")
	public List<DailyActivityPercentageDto> getLast7DaysActivityPercentage(@AuthenticationPrincipal CustomUserDetails currentUser) {
		
		String userId = currentUser.getId();
		
		return userService.getLast7DaysActivityPercentage(userId);
	}
	
	
	@GetMapping("/profile")
	public UserProfileDto getProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
		
		String userId = currentUser.getId();
		return userService.getProfile(userId);
	}
	
	
	@PutMapping("/profile/update")
	public String updateProfile(
			@RequestBody UserProfileDto dto,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		
		String userId = currentUser.getId();
		
		return userService.updateUserProfile(userId, dto);
	}
}
