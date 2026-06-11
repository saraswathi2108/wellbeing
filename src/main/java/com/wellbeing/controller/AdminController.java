package com.wellbeing.controller;


import com.wellbeing.dto.SubscriptionRequest;
import com.wellbeing.dto.SubscriptionResponse;
import com.wellbeing.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/create/subscription")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @RequestBody SubscriptionRequest request) {

        return ResponseEntity.ok(
                subscriptionService
                        .createSubscription(request));
    }
    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

}
