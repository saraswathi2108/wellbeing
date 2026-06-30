package com.wellbeing.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
	
	
	@Query("SELECT a FROM Activities a WHERE a.status = true AND " +
		       "((a.user.id = :userId AND a.createdAt BETWEEN :start AND :end) OR a.isDefault = true) " +
		       "ORDER BY a.isDefault DESC, a.createdAt DESC")
		List<Activities> findUserActivitiesAndDefaults(
		        @Param("userId") String userId, 
		        @Param("start") LocalDateTime start, 
		        @Param("end") LocalDateTime end);

		@Query("SELECT a FROM Activities a WHERE a.status = true AND a.activityType = :type AND " +
		       "((a.user.id = :userId AND a.createdAt BETWEEN :start AND :end) OR a.isDefault = true) " +
		       "ORDER BY a.isDefault DESC, a.createdAt DESC")
		List<Activities> findUserActivitiesAndDefaultsByType(
		        @Param("userId") String userId, 
		        @Param("type") ActivityType type, 
		        @Param("start") LocalDateTime start, 
		        @Param("end") LocalDateTime end);
}
