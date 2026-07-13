package com.wellbeing.service;

import com.wellbeing.ExceptionHandler.BadRequestException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.ExceptionHandler.UnauthorizedException;
import com.wellbeing.config.SecurityUtil;
import com.wellbeing.dto.SubscriptionRequest;
import com.wellbeing.dto.SubscriptionResponse;
import com.wellbeing.dto.SubscriptionUpdateReqDTO;
import com.wellbeing.entity.Subscription;
import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.SubscriptionRepository;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    
    
    private Integer calculateFinalPrice(Integer price, Integer discountPercentage) {
        if (price == null || price == 0) return 0;
        
        if (discountPercentage == null || discountPercentage <= 0) {
            return price; 
        }
        
        Integer discountAmount = (price * discountPercentage) / 100;
        return price - discountAmount;
    }

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
        subscription.setTrialPlan(request.getTrialPlan());
        
        Integer discount = request.getDiscountPercentage() != null ? request.getDiscountPercentage() : 0;
        subscription.setDiscountPercentage(discount);
        subscription.setFinalPrice(calculateFinalPrice(request.getPrice(), discount));

        Subscription saved =
                subscriptionRepository.save(subscription);

        return mapToResponse(saved);
    }

    private SubscriptionResponse mapToResponse(Subscription saved) {
    	
    	Integer discountAmt = saved.getPrice() - saved.getFinalPrice();

        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        subscriptionResponse.setSubId(saved.getSubId());
        subscriptionResponse.setSubName(saved.getSubName());
        subscriptionResponse.setSubDescription(saved.getSubDescription());
        subscriptionResponse.setPrice(saved.getPrice());
        subscriptionResponse.setFinalPrice(saved.getFinalPrice());
        subscriptionResponse.setDiscountAmount(discountAmt);
        subscriptionResponse.setDurationDays(saved.getDurationDays());
        subscriptionResponse.setStatus(saved.getStatus());
        return subscriptionResponse;

    }

    public List<SubscriptionResponse> getByStatus(Boolean status) {

        return subscriptionRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public String activateTrial(String subId) {

        String userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "User not authenticated"));

        Users user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        Subscription subscription =
                subscriptionRepository.findById(subId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription not found"));

        if (!Boolean.TRUE.equals(subscription.getTrialPlan())) {
            throw new BadRequestException(
                    "This is not a trial subscription");
        }

        List<UserSubscription> activeSubscriptions =
                userSubscriptionRepository
                        .findAllByUser_IdAndStatus(
                                userId,
                                UserSubscriptionStatus.ACTIVE);

        if (!activeSubscriptions.isEmpty()) {
            throw new BadRequestException(
                    "User already has an active subscription");
        }

        Long count =
                userSubscriptionRepository.count() + 1;

        String userSubId =
                String.format("US%03d", count);

        UserSubscription trialSubscription =
                new UserSubscription();

        trialSubscription.setUserSubId(userSubId);

        trialSubscription.setUser(user);

        trialSubscription.setSubscription(subscription);

        trialSubscription.setStatus(
                UserSubscriptionStatus.ACTIVE);

        trialSubscription.setStartDate(
                LocalDate.now());

        trialSubscription.setEndDate(
                LocalDate.now()
                        .plusDays(
                                subscription.getDurationDays()));

        userSubscriptionRepository.save(
                trialSubscription);

        return "Trial activated successfully";


    }

	public String changeStatus(String subId, Boolean status) {
		
		Subscription subscription = subscriptionRepository.findById(subId)
				.orElseThrow(() -> new ResourceNotFoundException("Subscription Not Found to change Status"));

		subscription.setStatus(status);
		subscriptionRepository.save(subscription);

		return "Subscription Status changed succesfully to: "+ status;
	}

	public String updateSub(String subId, SubscriptionUpdateReqDTO dto) {
		
		Subscription subscription = subscriptionRepository.findById(subId)
				.orElseThrow(() -> new ResourceNotFoundException("Subscription Not found to update"));
		
		if(dto.getDurationDays() != null) {
			subscription.setDurationDays(dto.getDurationDays());
		}
		
		if(dto.getPrice() != null) {
			subscription.setPrice(dto.getPrice());
		}
		
		if(dto.getSubDescription() != null) {
			subscription.setSubDescription(dto.getSubDescription());
		}
		
		if(dto.getSubName() != null) {
			subscription.setSubName(dto.getSubName());
		}
		
		boolean priceOrDiscountChanged = false;
        
        if (dto.getPrice() != null) {
            subscription.setPrice(dto.getPrice());
            priceOrDiscountChanged = true;
        }
        
        if (dto.getDiscountPercentage() != null) {
        	subscription.setDiscountPercentage(dto.getDiscountPercentage());
            priceOrDiscountChanged = true;
        }

        if (priceOrDiscountChanged) {
        	subscription.setFinalPrice(
                calculateFinalPrice(subscription.getPrice(), subscription.getDiscountPercentage())
            );
        }

        Subscription updatedSub = subscriptionRepository.save(subscription);
		
		subscriptionRepository.save(subscription);

		return "Subscription updated succesfully";
	}



}
