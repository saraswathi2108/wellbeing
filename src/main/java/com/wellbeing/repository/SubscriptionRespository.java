package com.wellbeing.repository;

import com.wellbeing.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRespository extends JpaRepository<Subscription, Integer> {
}
