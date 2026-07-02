package com.wellbeing.repository;

import com.wellbeing.entity.UserDeletedActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeletedActivityRepository extends JpaRepository<UserDeletedActivity, Long> {
    boolean existsByUserIdAndActivityId(String userId, String activityId);
}