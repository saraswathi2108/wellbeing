package com.wellbeing.config;

import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import com.wellbeing.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final UserSubscriptionRepository
            userSubscriptionRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireSubscriptions() {

        List<UserSubscription> activeSubscriptions =
                userSubscriptionRepository
                        .findByStatus(
                                UserSubscriptionStatus.ACTIVE);

        for (UserSubscription subscription :
                activeSubscriptions) {

            if (!subscription.getEndDate()
                    .isAfter(LocalDate.now())) {

                subscription.setStatus(
                        UserSubscriptionStatus.EXPIRED);

                userSubscriptionRepository
                        .save(subscription);
            }
        }

        List<UserSubscription> trialSubscriptions =
                userSubscriptionRepository
                        .findByStatus(
                                UserSubscriptionStatus.TRIAL);

        for (UserSubscription subscription :
                trialSubscriptions) {

            if (!subscription.getEndDate()
                    .isAfter(LocalDate.now())) {

                subscription.setStatus(
                        UserSubscriptionStatus.EXPIRED);

                userSubscriptionRepository
                        .save(subscription);
            }
        }
    }
}