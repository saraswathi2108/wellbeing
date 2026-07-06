package com.wellbeing.config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.wellbeing.entity.PrimaryRole;
import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.UserSubscriptionRepository;
import com.wellbeing.service.CustomUserDetails;
import com.wellbeing.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final UserSubscriptionRepository userSubscriptionRepository;
	
	
	private final String FRONTEND_REDIRECT_URL = "https://digital-57o6.onrender.com/oauth2/redirect?token=";
	

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		
		
		// extracting data from Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        log.info("OAuth2 login successful for email: {}", email);
        
        
        Users user = userRepository.findByEmail(email)
        		.orElseGet(() ->{
        			
        			Users newUser = new Users();
        			
        			Long count = userRepository.count() + 1;
                    String userId = String.format("USER%05d", count);
                    
                    newUser.setId(userId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole("USER"); // Default role
                    newUser.setCreatedAt(LocalDateTime.now());
                    
                    return userRepository.save(newUser);
                    
        		});
        
        CustomUserDetails userDetails = new CustomUserDetails(user);
        
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
        

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getAuthorities());
        extraClaims.put("userId", user.getId());
        extraClaims.put("name", user.getName());
        
        extraClaims.put("trialUsed", trialUsed);
        extraClaims.put("trialStatus", trialStatus);
        extraClaims.put("trialexpireDate", trialexpireDate);

        
        extraClaims.put("activePlanId", activePlanId);
        extraClaims.put("activePlanName", activePlanName);
        extraClaims .put("planStatus", userSubscriptionStatus);
        
        String token = jwtService.generateToken(extraClaims, userDetails);

        response.sendRedirect(FRONTEND_REDIRECT_URL + token);
        
	}

}
