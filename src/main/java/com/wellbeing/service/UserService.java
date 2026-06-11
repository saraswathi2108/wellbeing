package com.wellbeing.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.wellbeing.dto.ActivityAddDto;
import com.wellbeing.entity.Activities;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.ActivityRepository;
import com.wellbeing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final ActivityRepository activityRepository;
	private final UserRepository userRepository; 

	public String addActivity(ActivityAddDto activityAddDto, String userId) {
		
		if (activityRepository.findByActivityName(activityAddDto.getActivityName()).isPresent()) {
		    throw new RuntimeException("Activity already exists");
		}
		
		Users user = userRepository.findById(userId)
		        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
		
		Long count = activityRepository.count() + 1;
		String activityId = String.format("ACTIVITY%05d", count);
	
		Activities newActivity = new Activities();
		newActivity.setId(activityId);
		newActivity.setActivityName(activityAddDto.getActivityName());
		newActivity.setActivityPercentage(activityAddDto.getContributionPercentage());
		newActivity.setActivityType(activityAddDto.getActivityType());
		newActivity.setCreatedAt(LocalDateTime.now());
		newActivity.setStatus(true);
		
		newActivity.setUser(user); 

		activityRepository.save(newActivity);

		return "Activity Added Successfully with ID: " + activityId;
	}
}