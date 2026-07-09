package com.wellbeing.config;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.wellbeing.entity.Users;
import com.wellbeing.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		
		if(!userRepository.existsByEmail("admin@wellbeing.com")) {
			
			Long count = userRepository.count() + 1;

		    String userId = String.format("USER%05d", count);
			
			Users users = new Users();
			users.setId(userId);
			users.setEmail("admin@wellbeing.com");
			users.setPassword(passwordEncoder.encode("admin@123"));
			users.setName("admin");
			users.setAge(10);
			users.setGender("male");
			users.setRole("ADMIN");
			users.setWakeUpTime(LocalTime.now());
			users.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			users.setCreatedAt(LocalDateTime.now());
			
			userRepository.save(users);
						
			log.info("Default admin created");
		}
		
		log.info("admin data loaded");


		
	}

}
