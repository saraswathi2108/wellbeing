package com.wellbeing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wellbeing.ExceptionHandler.ConflictException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.ExceptionHandler.UnauthorizedException;
import com.wellbeing.dto.ActivityAddDto;
import com.wellbeing.dto.ActivityLogResponseDto;
import com.wellbeing.dto.ActivityResponseDto;
import com.wellbeing.dto.DailyActivityPercentageDto;
import com.wellbeing.dto.UserProfileDto;
import com.wellbeing.entity.Activities;
import com.wellbeing.entity.ActivityLogs;
import com.wellbeing.entity.ActivityType;
import com.wellbeing.entity.ScoreHistory;
import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import com.wellbeing.repository.ActivityLogsRepository;
import com.wellbeing.repository.ActivityRepository;
import com.wellbeing.repository.ScoreHistoryRepository;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.WellBeingScoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final ActivityRepository activityRepository;
	private final UserRepository userRepository;
	private final ActivityLogsRepository activityLogsRepository;
	private final WellBeingScoreRepository wellBeingScoreRepository;
	private final ScoreHistoryRepository scoreHistoryRepository;

	public String addActivity(ActivityAddDto activityAddDto, String userId) {
		
		if (activityRepository.findByActivityName(activityAddDto.getActivityName()).isPresent()) {
		    throw new ConflictException("Activity already exists");
		}
		
		Users user = userRepository.findById(userId)
		        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
		
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
	
	
	
	@Transactional // Very Important: Idi lekapothe DB sagam corrupt avthundi
    public String logCompletedActivity(String activityId, String userId) {
        
        // 1. Fetch User and Activity
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Activities activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        // User verification
        if (!activity.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("This activity does not belong to the user");
        }

        // 2. Calculate Score Change (Drain = minus, Recovery = plus)
        int scoreChange = activity.getActivityPercentage(); // Assuming this is the value
        if (activity.getActivityType() == ActivityType.DRAIN) {
            scoreChange = -Math.abs(scoreChange);
        } else {
            scoreChange = Math.abs(scoreChange);
        }


		Long count = activityLogsRepository.count() + 1;
		 
		String activityId2 = String.format("ACTIVITYLOGS%05d", count);
        
        // 3. Create and Save ActivityLog
        ActivityLogs activityLog = new ActivityLogs();
        activityLog.setId(activityId2);
        activityLog.setActivity(activity);
        activityLog.setUser(user);
        activityLog.setScoreChange(scoreChange);
        activityLog.setCreatedAt(LocalDateTime.now());
        activityLogsRepository.save(activityLog);

        // 4. Fetch or Initialize WellbeingScore
        WellbeingScore currentScoreObj = wellBeingScoreRepository.findByUser(user).orElse(null);
        int previousScore = 0;
        
        if (currentScoreObj == null) {
        	
        	Long count2 = wellBeingScoreRepository.count() + 1;
   		 
    		String wellbeingscoreId = String.format("WELLBEING%05d", count);
    		
            currentScoreObj = new WellbeingScore();
            currentScoreObj.setWellScoreId(wellbeingscoreId);
            currentScoreObj.setUser(user);
            currentScoreObj.setCurrentScore(0);
        } else {
            previousScore = currentScoreObj.getCurrentScore();
        }

        // 5. Update WellbeingScore
        int newScore = currentScoreObj.getCurrentScore() + scoreChange;
        currentScoreObj.setCurrentScore(newScore);
        currentScoreObj.setUpdatedAt(LocalDateTime.now());
        wellBeingScoreRepository.save(currentScoreObj);

        // 6. Map to ScoreHistory
        
        Long count3 = scoreHistoryRepository.count() + 1;
		 
		String scorehostoryId = String.format("SCOREHIS%05d", count);
        
        ScoreHistory history = new ScoreHistory();
        history.setScoreHistoryId(scorehostoryId);
        history.setUser(user);
        history.setWellbeingScore(currentScoreObj);
        history.setPreviousScore(previousScore);
        history.setNewScore(newScore);
        history.setRecordedAt(LocalDateTime.now());
        history.setActivitie(activity); 
        scoreHistoryRepository.save(history);

        return "Activity logged successfully. Previous Score: " + previousScore + ", New Score: " + newScore;
    }



	public List<ActivityResponseDto> getActivities(ActivityType activityType, String userId) {
		
		// 1. User Validation
		Users users = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found to get Activites"));

		List<Activities> activities;
		
		if (activityType == null) {
			activities = activityRepository.findByUserIdAndStatusTrue(userId);
		} else {
			activities = activityRepository.findByUserIdAndActivityTypeAndStatusTrue(userId, activityType);
		}
		
		return activities.stream()
				.map(act -> {
				ActivityResponseDto dto = new ActivityResponseDto();
				dto.setActivityId(act.getId());
				dto.setActivityName(act.getActivityName());
				dto.setActivityType(act.getActivityType());
				dto.setActivityPercenage(act.getActivityPercentage()); 
				dto.setStatus(act.getStatus());
				dto.setCreatedAt(act.getCreatedAt());
				return dto;
			}).toList();
	}



	public String deleteActivity(String activityId, Boolean status) {
		
		Activities activities = activityRepository.findById(activityId)
				.orElseThrow(() -> new ResourceNotFoundException("Activity Not Found to delete"));
		
		activities.setStatus(status);
		
		activityRepository.save(activities);

		return "Activity deleted Succesfully";
	}
	
	
	
	public List<ActivityLogResponseDto> getRecentActivities(String userId) {
		
		userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

		return activityLogsRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId)
				.stream()
				.map(log -> ActivityLogResponseDto.builder()
						.activityLogId(log.getId())
						.activityName(log.getActivity().getActivityName())
						.activityType(log.getActivity().getActivityType().name())
						.scoreChange(log.getScoreChange())
						.completedAt(log.getCreatedAt())
						.build())
				.toList();
	}
	
	
	public List<DailyActivityPercentageDto> getLast7DaysActivityPercentage(String userId) {
		
		LocalDateTime endDate = LocalDateTime.now();
		LocalDateTime startDate = endDate.minusDays(7); 

		List<ActivityLogs> logs = activityLogsRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);

		// 3. Date prakaram group chesi, aa roju chesina percentage add cheyi 
        // (DRAIN aina RECOVERY aina percentage matram absolute value e untundi)
		Map<LocalDate, Integer> dailyStats = logs.stream()
				.collect(Collectors.groupingBy(
						log -> log.getCreatedAt().toLocalDate(),
						Collectors.summingInt(log -> Math.abs(log.getScoreChange())) 
				));

		// 4. Missing days cover chey (Frontend chart break avvakunda)
		List<DailyActivityPercentageDto> result = new ArrayList<>();
		for (int i = 6; i >= 0; i--) {
			LocalDate date = endDate.minusDays(i).toLocalDate();
			result.add(DailyActivityPercentageDto.builder()
					.date(date)
					.totalPercentage(dailyStats.getOrDefault(date, 0))
					.build());
		}

		return result;
	}



	public UserProfileDto getProfile(String userId) {
		
		Users users = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found to get Activites"));

		
		return UserProfileDto.builder()
				.userId(users.getId())
				.name(users.getName())
				.email(users.getEmail())
				.age(users.getAge())
				.gender(users.getGender())
				.primaryRole(users.getPrimaryRole())
				.wakeUpTime(users.getWakeUpTime())
				.timeZone(users.getTimeZone())
				.build();
	}
	
	
	
	public String updateUserProfile(String userId, UserProfileDto dto) {
		
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Null checks: Frontend nunchi vachina data null kakapothe matrame update chey
		if (dto.getName() != null) {
			user.setName(dto.getName());
		}
		if (dto.getAge() != null) {
			user.setAge(dto.getAge());
		}
		if (dto.getGender() != null) {
			user.setGender(dto.getGender());
		}
		if (dto.getPrimaryRole() != null) {
			user.setPrimaryRole(dto.getPrimaryRole());
		}
		if (dto.getWakeUpTime() != null) {
			user.setWakeUpTime(dto.getWakeUpTime());
		}

		userRepository.save(user);

		return "Profile updated successfully";
	}
}