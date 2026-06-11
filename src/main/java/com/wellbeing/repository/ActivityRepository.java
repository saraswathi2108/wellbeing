package com.wellbeing.repository;

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
}
