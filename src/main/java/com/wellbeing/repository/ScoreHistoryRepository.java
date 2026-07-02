package com.wellbeing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.ScoreHistory;


@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, String>{
	
	@Query("SELECT AVG(s.newScore) FROM ScoreHistory s WHERE s.user.id = :userId AND s.recordedAt BETWEEN :start AND :end")
	Double getAverageScoreByUserIdAndMonth(
	        @Param("userId") String userId, 
	        @Param("start") LocalDateTime start, 
	        @Param("end") LocalDateTime end);

	
//	last 7 days
	List<ScoreHistory> findByUserIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            String userId, LocalDateTime start, LocalDateTime end);
}
