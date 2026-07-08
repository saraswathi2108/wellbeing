package com.wellbeing.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wellbeing.ExceptionHandler.ConflictException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.ChangePasswordDTO;
import com.wellbeing.dto.LoginDto;
import com.wellbeing.dto.ResetPasswordDTO;
import com.wellbeing.service.AuthService;
import com.wellbeing.service.CustomUserDetails;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.wellbeing.dto.UserRegisterDTO;
import com.wellbeing.dto.VerifyOtpDTO;
import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.UserSubscriptionRepository;
import com.wellbeing.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
//	private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final com.wellbeing.service.CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserSubscriptionRepository userSubscriptionRepository;
    
    

	@PostMapping("/login")
	    public String loginUser(@RequestBody LoginDto dto) {
	        authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        dto.getEmail(),
	                        dto.getPassword()
	                )
	        );

	        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());

	        Users user = userRepository.findByEmail(dto.getEmail())
	                .orElseThrow(() -> new ResourceNotFoundException("User not found after successful authentication"));
	        
	        if(user.getIs_active().equals(false)) {
	        	throw new ConflictException("You're account has been deleted");
	        }

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
	        extraClaims .put("expireDate", expireDate);

	        
	        log.info("User logged in Succesfully: {}", dto.getEmail());
	        return jwtService.generateToken(extraClaims, userDetails);
	    }

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterDTO dto, @RequestParam String otp) {
        return authService.registerUser(dto, otp);
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        return authService.sendRegistrationOtp(email);
    }
    
    
    @PostMapping("/forget-Password")
    public String forgetPassword(@RequestParam String email) {
    	return authService.sendForgetOtp(email);
    }
    
    
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody VerifyOtpDTO dto) {
        return authService.verifyOtp(dto);
    }

    
    @PostMapping("/reset-Password")
    public String resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
    	return authService.resetpassword(resetPasswordDTO);
    }
    
    
    @PostMapping("/change-Password")
    public String changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
    							@AuthenticationPrincipal CustomUserDetails currentUser) {
    	
    	String userId = currentUser.getId();
    	return authService.changePassword(changePasswordDTO, userId);
    }
    
    
    @PutMapping("/delete")
    public String deactivateAccount(@RequestParam String email) {
    	
    	return authService.deactivateAccount(email);
    }

}

