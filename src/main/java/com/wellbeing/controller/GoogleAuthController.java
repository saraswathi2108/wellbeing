package com.wellbeing.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.UserSubscriptionRepository;
import com.wellbeing.repository.WellBeingScoreRepository;
import com.wellbeing.service.CustomUserDetails;
import com.wellbeing.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final WellBeingScoreRepository wellBeingScoreRepository;

//    private static final String CLIENT_ID = "341709776135-2gc8sr7belb8if4d19sse44dkvlr9rh7.apps.googleusercontent.com";
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String googleToken = request.get("token");

        try {
            // 1. Google ID Token ni verify chesthunnam
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            		.setAudience(Arrays.asList(CLIENT_ID)) 
                    .build();

            GoogleIdToken idToken = verifier.verify(googleToken);
            
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                
                // 2. Extract Data
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                
                log.info("Google Native login successful for email: {}", email);

                // 3. Mee Patha Logic: Find User or Create New User
                Users user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            Users newUser = new Users();
                            Long count = userRepository.count() + 1;
                            String userId = String.format("USER%05d", count);
                            
                            newUser.setId(userId);
                            newUser.setEmail(email);
                            newUser.setName(name);
                            newUser.setIs_active(true);
                            newUser.setRole("USER"); // Default role
                            newUser.setCreatedAt(LocalDateTime.now());
                            
                            Users savedUser = userRepository.save(newUser);
                            
                            Long scoreCount = wellBeingScoreRepository.count() + 1;
                            WellbeingScore initialScore = new WellbeingScore();
                            initialScore.setWellScoreId(String.format("WELLBEING%05d", scoreCount));
                            initialScore.setUser(savedUser); 
                            initialScore.setCurrentScore(100);
                            initialScore.setUpdatedAt(LocalDateTime.now());
                            
                            wellBeingScoreRepository.save(initialScore);
                            
                            return savedUser;
                        });

                CustomUserDetails userDetails = new CustomUserDetails(user);

                // 4. Mee Patha Logic: Subscription Check
                List<UserSubscription> userSubscriptions = userSubscriptionRepository.findByUserId(user.getId());
                boolean trialUsed = false;
                UserSubscriptionStatus trialStatus = UserSubscriptionStatus.EXPIRED;
                String trialexpireDate = null;
                
                String activePlanId = null;
                String activePlanName = null;
                UserSubscriptionStatus userSubscriptionStatus = UserSubscriptionStatus.EXPIRED;
                String expireDate = null;

                for (UserSubscription us : userSubscriptions) {
                    if (Boolean.TRUE.equals(us.getSubscription().getTrialPlan())) {
                        trialUsed = true; 
                        trialStatus = us.getStatus();
                        trialexpireDate = us.getEndDate().toString();
                    }
                    if (us.getStatus() == UserSubscriptionStatus.ACTIVE) {
                        activePlanId = us.getSubscription().getSubId();
                        activePlanName = us.getSubscription().getSubName();
                        userSubscriptionStatus = us.getStatus();
                        expireDate = us.getEndDate().toString();
                    }
                }

                // 5. Mee Patha Logic: Extra Claims add cheyadam
                Map<String, Object> extraClaims = new HashMap<>();
                extraClaims.put("role", userDetails.getAuthorities());
                extraClaims.put("userId", user.getId());
                extraClaims.put("name", user.getName());
                extraClaims.put("trialUsed", trialUsed);
                extraClaims.put("trialStatus", trialStatus);
                extraClaims.put("trialexpireDate", trialexpireDate);
                extraClaims.put("activePlanId", activePlanId);
                extraClaims.put("activePlanName", activePlanName);
                extraClaims.put("planStatus", userSubscriptionStatus);
                extraClaims.put("expireDate", expireDate);

                // 6. Generate JWT Token
                String token = jwtService.generateToken(extraClaims, userDetails);

                // 7. 🔥 FRONTEND KI REDIRECT KAKUNDA JSON RESPONSE PAMPALI
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("token", token);
                responseBody.put("message", "Login Successful");

                return ResponseEntity.ok(responseBody);
                
            } else {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid Google ID token."));
            }
        } catch (Exception e) {
            log.error("Error verifying token", e);
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error verifying token"));
        }
    }
}