package com.wellbeing.service;

import com.wellbeing.dto.SubscriptionRequest;
import com.wellbeing.dto.SubscriptionResponse;
import com.wellbeing.entity.Subscription;
import com.wellbeing.repository.SubscriptionRespository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRespository subscriptionRepository;

    public SubscriptionResponse createSubscription(
            SubscriptionRequest request) {

        Long count = subscriptionRepository.count() + 1;

        String subId = String.format("SUB%02d", count);

        Subscription subscription = new Subscription();

        subscription.setSubId(subId);
        subscription.setSubName(request.getSubName());
        subscription.setSubDescription(request.getSubDescription());
        subscription.setPrice(request.getPrice());
        subscription.setDurationDays(request.getDurationDays());
        subscription.setStatus(true);

        Subscription saved =
                subscriptionRepository.save(subscription);

        return mapToResponse(saved);
    }

    private SubscriptionResponse mapToResponse(Subscription saved) {

        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        subscriptionResponse.setSubId(saved.getSubId());
        subscriptionResponse.setSubName(saved.getSubName());
        subscriptionResponse.setSubDescription(saved.getSubDescription());
        subscriptionResponse.setPrice(saved.getPrice());
        subscriptionResponse.setDurationDays(saved.getDurationDays());
        subscriptionResponse.setStatus(saved.getStatus());
        return subscriptionResponse;

    }

    public List<SubscriptionResponse> getAllSubscriptions() {

        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


}
