package com.wellbeing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.wellbeing.ExceptionHandler.BadRequestException;
import com.wellbeing.ExceptionHandler.ConflictException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.ExceptionHandler.UnauthorizedException;
import com.wellbeing.dto.ActivityAddDto;
import com.wellbeing.dto.ActivityLogResponseDto;
import com.wellbeing.dto.ActivityResponseDto;
import com.wellbeing.dto.AdminConsultationResponseDto;
import com.wellbeing.dto.AdminUserMonthlyStatsDto;
import com.wellbeing.dto.DailyActivityPercentageDto;
import com.wellbeing.dto.MostUsedActivitiesDto;
import com.wellbeing.dto.PlanUsersResponseDto;
import com.wellbeing.dto.SubscriptionAnalyticsDto;
import com.wellbeing.dto.UserProfileDto;
import com.wellbeing.entity.Activities;
import com.wellbeing.entity.ActivityLogs;
import com.wellbeing.entity.ActivityType;
import com.wellbeing.entity.AppConfiguration;
import com.wellbeing.entity.ConsultationBooking;
import com.wellbeing.entity.PaymentStatus;
import com.wellbeing.entity.PrimaryRole;
import com.wellbeing.entity.ScoreHistory;
import com.wellbeing.entity.Subscription;
import com.wellbeing.entity.UserDeletedActivity;
import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import com.wellbeing.repository.ActivityLogsRepository;
import com.wellbeing.repository.ActivityRepository;
import com.wellbeing.repository.AppConfigurationRepo;
import com.wellbeing.repository.ConsultationBookingRepo;
import com.wellbeing.repository.ScoreHistoryRepository;
import com.wellbeing.repository.SubscriptionRepository;
import com.wellbeing.repository.UserDeletedActivityRepository;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.WellBeingScoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	
	private final ActivityRepository activityRepository;
	private final UserRepository userRepository;
	private final ActivityLogsRepository activityLogsRepository;
	private final WellBeingScoreRepository wellBeingScoreRepository;
	private final ScoreHistoryRepository scoreHistoryRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final UserDeletedActivityRepository userDeletedActivityRepository;
	private final ConsultationBookingRepo consultationBookingRepo;
	private final AppConfigurationRepo appConfigurationRepo;

	
	@Transactional
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
		
		logCompletedActivity(activityId, userId);

		return "Activity Added Successfully with ID: " + activityId;
	}
	
	
	
	
	@Transactional
    public String logCompletedActivity(String activityId, String userId) {
        
        // 1. Fetch User and Activity
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Activities activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        // User verification
//        if (!activity.getUser().getId().equals(userId)) {
//            throw new UnauthorizedException("This activity does not belong to the user");
//        }

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
            String wellbeingscoreId = String.format("WELLBEING%05d", count2);
                 
            currentScoreObj = new WellbeingScore();
            currentScoreObj.setWellScoreId(wellbeingscoreId);
            currentScoreObj.setUser(user);
            currentScoreObj.setCurrentScore(0);
        } else {
            previousScore = currentScoreObj.getCurrentScore();
        }

        // 5. Update WellbeingScore
        int calculatedScore = currentScoreObj.getCurrentScore() + scoreChange;
        int newScore = Math.max(0, Math.min(100, calculatedScore)); 

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

	    // 2. Timezone specific today's boundaries calculation (Blind spot fixed)
	    ZoneId istZone = ZoneId.of("Asia/Kolkata");
	    LocalDateTime startOfDay = LocalDate.now(istZone).atStartOfDay();
	    LocalDateTime endOfDay = LocalDate.now(istZone).atTime(LocalTime.MAX);

	    List<Activities> activities;
	    
	    // 3. Fetch data based on today's timeframe
	    if (activityType == null) {
	        activities = activityRepository.findUserActivitiesAndDefaults(
	            userId, startOfDay, endOfDay
	        );
	    } else {
	        activities = activityRepository.findUserActivitiesAndDefaultsByType(
	            userId, activityType, startOfDay, endOfDay
	        );
	    }
	    
	    
	    
//	    List<Activities> activities;
//		
//		if (activityType == null) {
//			activities = activityRepository.findByUserIdAndStatusTrue(userId);
//		} else {
//			activities = activityRepository.findByUserIdAndActivityTypeAndStatusTrue(userId, activityType);
//		}
	    
	    // 4. Map to DTO
	    return activities.stream()
	            .map(act -> {
	            ActivityResponseDto dto = new ActivityResponseDto();
	            dto.setActivityId(act.getId());
	            dto.setActivityName(act.getActivityName());
	            dto.setActivityType(act.getActivityType());
	            dto.setActivityPercenage(act.getActivityPercentage()); 
	            dto.setStatus(act.getStatus());
	            dto.setCreatedAt(act.getCreatedAt());
	            dto.setIsDefault(act.getIsDefault());
	            return dto;
	        }).toList();
	}

	


	@Transactional
	public String deleteActivity(String activityId, String userId) {
		
		Activities activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ResourceNotFoundException("Activity Not Found to delete"));
		
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));


		if (Boolean.FALSE.equals(activity.getIsDefault()) && activity.getUser() != null) {
			if (!activity.getUser().getId().equals(userId)) {
				throw new UnauthorizedException("This activity does not belong to you");
			}
			activity.setStatus(false);
			activityRepository.save(activity);
		} 
		// Condition 2: Shared Default Activity aithe complete table lo drop cheyakunda user list mapping pettu
		else {
			boolean alreadyDeleted = userDeletedActivityRepository.existsByUserIdAndActivityId(userId, activityId);
			if (!alreadyDeleted) {
				UserDeletedActivity deletedActivity = new UserDeletedActivity();
				deletedActivity.setUser(user);
				deletedActivity.setActivity(activity);
				userDeletedActivityRepository.save(deletedActivity);
			}
		}

		return "Activity deleted successfully for this user";
	}
	
	
	
	public List<ActivityLogResponseDto> getRecentActivities(String userId) {
		
		userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		
		// 2. Timezone specific today's boundaries calculation (Blind spot fixed)
		
	    ZoneId istZone = ZoneId.of("Asia/Kolkata");
	    LocalDateTime startOfDay = LocalDate.now(istZone).atStartOfDay();
	    LocalDateTime endOfDay = LocalDate.now(istZone).atTime(LocalTime.MAX);

		return activityLogsRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startOfDay, endOfDay)
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
		
        // 1. Exact Timezone Calculation
		ZoneId istZone = ZoneId.of("Asia/Kolkata");
		LocalDateTime endDate = LocalDateTime.now(istZone);
		LocalDateTime startDate = endDate.minusDays(7).with(LocalTime.MIN); 

        // 2. Fetch all score changes for the last 7 days in Ascending order
		List<ScoreHistory> historyLogs = scoreHistoryRepository
                .findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(userId, startDate, endDate);


		Map<LocalDate, Integer> dailyLatestScores = historyLogs.stream()
				.collect(Collectors.toMap(
						log -> log.getRecordedAt().toLocalDate(),
						log -> log.getNewScore(),
						(existing, replacement) -> replacement 
				));
		
		List<DailyActivityPercentageDto> result = new ArrayList<>();
		
		for (int i = 6; i >= 0; i--) {
			LocalDate date = endDate.minusDays(i).toLocalDate();
            
            Integer finalScoreForDay = dailyLatestScores.getOrDefault(date, 100);
            
			result.add(DailyActivityPercentageDto.builder()
					.date(date)
					.totalPercentage(finalScoreForDay)
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
				.phoneNo(users.getPhoneNo())
				.guardianName(users.getGuardianName())
				.guardianPhoneNo(users.getGuardianPhoneNo())
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
		if (dto.getPhoneNo() != null) {
			user.setPhoneNo(dto.getPhoneNo());
		}
		if (dto.getGuardianName() != null) {
			user.setGuardianName(dto.getGuardianName());
		}
		if (dto.getGuardianPhoneNo() != null) {
			user.setGuardianPhoneNo(dto.getGuardianPhoneNo());
		}

		userRepository.save(user);

		return "Profile updated successfully";
	}
	
	
	
	
	@Scheduled(cron = "0 0 0 * * ?") // Daily midnight
	@Transactional
	public void wellBeingScheduler() {

	    List<WellbeingScore> scores = wellBeingScoreRepository.findAll();

	    scores.forEach(score -> score.setCurrentScore(100));

	    wellBeingScoreRepository.saveAll(scores);

	    log.info("Wellbeing scores reset successfully");
	}
	
	
	
	
	public MostUsedActivitiesDto getMostUsedActivitiesForWeek(String userId) {
		
	    ZoneId istZone = ZoneId.of("Asia/Kolkata");
	    LocalDateTime startDate = LocalDateTime.now(istZone).minusDays(7);
	    
	    List<String> drainResult = activityLogsRepository.findMostUsedActivityTypeForUser(
	        userId, ActivityType.DRAIN, startDate, PageRequest.of(0, 1)
	    );
	    
	    List<String> recoveryResult = activityLogsRepository.findMostUsedActivityTypeForUser(
	        userId, ActivityType.RECOVERY, startDate, PageRequest.of(0, 1)
	    );

	    return MostUsedActivitiesDto.builder()
	        .mostUsedDrain(drainResult.isEmpty() ? "No Drain Activities Logged" : drainResult.get(0))
	        .mostUsedRecovery(recoveryResult.isEmpty() ? "No Recovery Activities Logged" : recoveryResult.get(0))
	        .build();
	}
	



	public AdminUserMonthlyStatsDto getMonthlyReportOfUser(String userId, int month, int year) {
		
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found With id: "+ userId));
		
		
		YearMonth yearMonth = YearMonth.of(year, month);
	    LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
	    LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
	    
	    
//	    Get Average Score
	    Double avgScore = scoreHistoryRepository.getAverageScoreByUserIdAndMonth(userId, startOfMonth, endOfMonth);


	 // Get Most Drained Activity
	    List<String> drainResult = activityLogsRepository.findMostLoggedActivityForMonth(
	            userId, ActivityType.DRAIN, startOfMonth, endOfMonth, PageRequest.of(0, 1)
	    );

	    
	  // Get Most Recovered Activity
	    List<String> recoveryResult = activityLogsRepository.findMostLoggedActivityForMonth(
	            userId, ActivityType.RECOVERY, startOfMonth, endOfMonth, PageRequest.of(0, 1)
	    );
	    
	    
	    
	    log.info("Fetching user: {} monthly stats", userId);
	    
	    return AdminUserMonthlyStatsDto.builder()
	            .averageWellbeingScore(avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0)
	            .mostDrainedActivity(drainResult.isEmpty() ? "No Drain Activities Logged" : drainResult.get(0))
	            .mostRecoveredActivity(recoveryResult.isEmpty() ? "No Recovery Activities Logged" : recoveryResult.get(0))
	            .build();
	}
	
	
	
	
	
	public List<SubscriptionAnalyticsDto> getSubscriptionAnalytics() {
		
	    List<Subscription> subscriptions = subscriptionRepository.findAll();
	    

	    return subscriptions.stream().map(sub -> {
	    	
        List<String> userIds = sub.getUserSubscriptions().stream()
	        		.filter(us -> us.getUser() != null)
					.map(us -> us.getUser().getId())
					.distinct()
					.toList();

			return SubscriptionAnalyticsDto.builder()
					.subId(sub.getSubId())
					.subName(sub.getSubName())
					.price(sub.getPrice())
					.totalPurchases((long) userIds.size())
					.userIds(userIds).build();
		}).toList();
	}
	
	

	public List<UserProfileDto> getAllUsers(Pageable pageable) {

		Page<Users> users = userRepository.findAll(pageable);

		if (users.isEmpty()) {
			throw new ResourceNotFoundException("Users Not Found");
		}

//		return users
//		        .stream()
//		        .map(user -> UserProfileDto.builder()
//		                .userId(user.getId())
//		                .name(user.getName())
//		                .email(user.getEmail())
//		                .age(user.getAge())
//		                .gender(user.getGender())
//		                .primaryRole(user.getPrimaryRole())
//		                .wakeUpTime(user.getWakeUpTime())
//		                .phoneNo(user.getPhoneNo())
//		                .guardianName(user.getGuardianName())
//		                .guardianPhoneNo(user.getGuardianPhoneNo())
//		                .build())
//		        .toList();

		return users.stream().sorted(Comparator.comparing(Users::getName).reversed()).map(user -> {
			UserProfileDto dto = new UserProfileDto();
			dto.setUserId(user.getId());
			dto.setName(user.getName());
			dto.setEmail(user.getEmail());
			dto.setAge(user.getAge());
			dto.setGender(user.getGender());
			dto.setPrimaryRole(user.getPrimaryRole());
			dto.setWakeUpTime(user.getWakeUpTime());
			dto.setPhoneNo(user.getPhoneNo());
			dto.setGuardianName(user.getGuardianName());
			dto.setGuardianPhoneNo(user.getGuardianPhoneNo());

			return dto;

		}).toList();
	}
	
	

	public List<AdminConsultationResponseDto> getConsultants(Pageable pageable) {

		List<ConsultationBooking> bookings = consultationBookingRepo
				.findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus.SUCCESSFUL, pageable);

		return bookings.stream()
				.map(booking -> AdminConsultationResponseDto.builder().bookingId(booking.getId())
						.userId(booking.getUser().getId()).userName(booking.getUser().getName())
						.userAge(booking.getUser().getAge()).userGender(booking.getUser().getGender())
						.registeredPhone(booking.getUser().getPhoneNo()).whatsappNumber(booking.getWhatsappNumber())
						.occupation(booking.getOccupation()).city(booking.getCity())
						.difficulties(booking.getDifficulties().stream().map(Enum::name).toList())
						.duration(booking.getDuration().name()).amountPaid(booking.getAmount())
						.bookedAt(booking.getCreatedAt()).adminInteracted(booking.getAdminInteracted()).build())
				.toList();
	}

	
	
	public String updateFee(Integer newFee) {

		AppConfiguration appConfiguration = appConfigurationRepo.findById("CONSULTATION_FEE")
				.orElse(new AppConfiguration());

		appConfiguration.setConfigKey("CONSULTATION_FEE");
		appConfiguration.setConfigValue(String.valueOf(newFee));
		appConfiguration.setDescription("dynamic consultation fee");

		appConfigurationRepo.save(appConfiguration);

		return "Fee updated Succesfully";

	}
	
	
	

	public String markAsConsulted(Long bookingId) {

		ConsultationBooking booking = consultationBookingRepo.findById(bookingId).orElseThrow(
				() -> new ResourceNotFoundException("Booking Id not Found to mark as Consult: " + bookingId));

		if (booking.getAdminInteracted() == true) {
			throw new BadRequestException("Already marked as Interacted");
		}
		
		if(!booking.getPaymentStatus().equals(PaymentStatus.SUCCESSFUL)) {
			throw new ConflictException("Only payment success booking can marked as consulted");
		}

		booking.setAdminInteracted(true);
		consultationBookingRepo.save(booking);

		log.info("Booking {} marked as admin Interacted", bookingId);

		return "Booking marked as Consulted";
	}




	public List<PlanUsersResponseDto> getUsersByPlan(List<String> userIds) {
		
		List<Users> users = userRepository.findByIdIn(userIds);
		
		if(users.isEmpty()) {
			throw new ResourceNotFoundException("No user's Found in this Plan");
		}
		
		
		return users.stream()
				.map(user -> PlanUsersResponseDto.builder()
						.userId(user.getId())
						.name(user.getName())
						.email(user.getEmail())
						.phoneNo(user.getPhoneNo())
						.role(user.getRole())
						.gender(user.getGender())
						.build())
				.toList();

		
	}

}