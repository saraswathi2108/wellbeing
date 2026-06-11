package com.wellbeing.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.wellbeing.dto.ActivityAddDto;
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
import com.wellbeing.repository.WellbeingScoreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final ActivityRepository activityRepository;
	private final UserRepository userRepository;
	private final ActivityLogsRepository activityLogsRepository;
	private final WellbeingScoreRepository wellBeingScoreRepository;
	private final ScoreHistoryRepository scoreHistoryRepository;

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
	
	
	
	@Transactional // Very Important: Idi lekapothe DB sagam corrupt avthundi
    public String logCompletedActivity(String activityId, String userId) {
        
        // 1. Fetch User and Activity
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        Activities activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // User verification
        if (!activity.getUser().getId().equals(userId)) {
            throw new RuntimeException("This activity does not belong to the user");
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
}