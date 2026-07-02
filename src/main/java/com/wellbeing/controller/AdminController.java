package com.wellbeing.controller;


import com.wellbeing.dto.AdminConsultationResponseDto;
import com.wellbeing.dto.AdminUserMonthlyStatsDto;
import com.wellbeing.dto.SubscriptionAnalyticsDto;
import com.wellbeing.dto.SubscriptionRequest;
import com.wellbeing.dto.SubscriptionResponse;
import com.wellbeing.dto.UserProfileDto;
import com.wellbeing.entity.Users;
import com.wellbeing.service.SubscriptionService;
import com.wellbeing.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;

    @PostMapping("/create/subscription")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @RequestBody SubscriptionRequest request) {

        return ResponseEntity.ok(
                subscriptionService
                        .createSubscription(request));
    }
    @GetMapping("allSubscriptions")
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }


    @PostMapping("/activate-trial/{subId}")
    public ResponseEntity<String> activateTrial(
            @PathVariable String subId) {

        return ResponseEntity.ok(
                subscriptionService.activateTrial(subId));
    }
    
    
    
    @GetMapping("/user/{userId}/monthlyStats")
    public AdminUserMonthlyStatsDto getUserMonthlyStats(@PathVariable String userId,
    													@RequestParam int month,
    													@RequestParam int year) {
    	
    	return userService.getMonthlyReportOfUser(userId, month, year);
    }
    
    
    
    @GetMapping("/subAnalytics")
    public List<SubscriptionAnalyticsDto> getSubscriptionAnalytics(){
    	
    	return userService.getSubscriptionAnalytics();
    }
    
    
    
    
    @GetMapping("/getAllUsers")
    public List<UserProfileDto> getAllUsers(
    							@RequestParam (defaultValue = "0") int page,
    							@RequestParam (defaultValue = "10") int size){
    	
    	Pageable pageable = PageRequest.of(page, size);
    	
    	return userService.getAllUsers(pageable);
    }
    
    
    
    @GetMapping("/getConsultations")
    public List<AdminConsultationResponseDto> getConsultations(
    							@RequestParam (defaultValue = "0") int page,
    							@RequestParam (defaultValue = "15") int size){
    	
    	Pageable pageable = PageRequest.of(page, size);
    	
    	return userService.getConsultants(pageable);
    }
}
