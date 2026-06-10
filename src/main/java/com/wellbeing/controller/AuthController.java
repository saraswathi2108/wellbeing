package com.wellbeing.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wellbeing.dto.LoginDto;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.UserRepository;
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
	                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));
	        
	        Map<String, Object> extraClaims = new HashMap<>();
	        extraClaims.put("role", userDetails.getAuthorities());
	        extraClaims.put("userId", user.getId());
	        
	        log.info("User logged in Succesfully: {}", dto.getEmail());
	        return jwtService.generateToken(extraClaims, userDetails);
	    }
	
}

