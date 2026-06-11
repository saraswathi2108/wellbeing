package com.wellbeing.repository;

import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, String> {
    Optional<UserSubscription> findByUserId(String userId);

    List<UserSubscription> findByStatus(UserSubscriptionStatus userSubscriptionStatus);
}
