package com.wellbeing.repository;

import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, String> {
    List<UserSubscription> findByUserId(String userId);

    @Query("""
       SELECT us
       FROM UserSubscription us
       WHERE us.user.id = :userId
       AND us.status = 'ACTIVE'
       """)
    Optional<UserSubscription> findActiveSubscription(String userId);
    List<UserSubscription> findByStatus(UserSubscriptionStatus userSubscriptionStatus);

    List<UserSubscription> findAllByUser_IdAndStatus(
            String userId,
            UserSubscriptionStatus status);}
