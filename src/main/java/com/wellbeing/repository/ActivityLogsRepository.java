package com.wellbeing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.ActivityLogs;
import com.wellbeing.entity.ActivityType;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, String> {
	
//	List<ActivityLogs> findTop5ByUserIdOrderByCreatedAtDesc(String userId);
	
	List<ActivityLogs> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(String userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
	
	List<ActivityLogs> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
	
	
	@Query("SELECT al.activity.activityName FROM ActivityLogs al " +
		       "WHERE al.user.id = :userId AND al.activity.activityType = :type " +
		       "AND al.createdAt >= :startDate " +
		       "GROUP BY al.activity.activityName " +
		       "ORDER BY COUNT(al.id) DESC")
		List<String> findMostUsedActivityTypeForUser(
		        @Param("userId") String userId, 
		        @Param("type") ActivityType type, 
		        @Param("startDate") LocalDateTime startDate, 
		        Pageable pageable);
	
	
	
//	Most Logged activity by the user for the month
	@Query("SELECT al.activity.activityName FROM ActivityLogs al " +
		       "WHERE al.user.id = :userId AND al.activity.activityType = :type " +
		       "AND al.createdAt BETWEEN :start AND :end " +
		       "GROUP BY al.activity.activityName " +
		       "ORDER BY COUNT(al.id) DESC")
		List<String> findMostLoggedActivityForMonth(
		        @Param("userId") String userId, 
		        @Param("type") ActivityType type, 
		        @Param("start") LocalDateTime start, 
		        @Param("end") LocalDateTime end, 
		        Pageable pageable);

}
