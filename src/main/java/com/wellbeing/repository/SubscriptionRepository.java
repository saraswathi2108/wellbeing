package com.wellbeing.repository;

import com.wellbeing.dto.SubscriptionResponse;
import com.wellbeing.entity.Subscription;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

	Collection<Subscription> findByStatus(Boolean status);
}
