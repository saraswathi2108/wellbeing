package com.wellbeing.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.Activities;
import com.wellbeing.entity.ActivityType;

@Repository
public interface ActivityRepository extends JpaRepository<Activities, String> {

	Optional<Activities> findByActivityName(String activityName);

//	by activityType
	List<Activities> findByUserIdAndActivityTypeAndStatusTrue(String userId, ActivityType activityType);
	
//	all activities
	List<Activities> findByUserIdAndStatusTrue(String userId);
	
	
	List<Activities> findByUserIdAndStatusTrueAndCreatedAtBetweenOrderByCreatedAtDesc(
		    String userId, LocalDateTime start, LocalDateTime end);

	List<Activities> findByUserIdAndActivityTypeAndStatusTrueAndCreatedAtBetweenOrderByCreatedAtDesc(
		    String userId, ActivityType activityType, LocalDateTime start, LocalDateTime end);
}
