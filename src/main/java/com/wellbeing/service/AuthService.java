package com.wellbeing.service;

import com.wellbeing.ExceptionHandler.AlreadyExistsException;
import com.wellbeing.ExceptionHandler.BadRequestException;
import com.wellbeing.ExceptionHandler.ForbiddenException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.ChangePasswordDTO;
import com.wellbeing.dto.ResetPasswordDTO;
import com.wellbeing.dto.UserRegisterDTO;
import com.wellbeing.dto.VerifyOtpDTO;
import com.wellbeing.entity.Otp;
import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.OtpRepository;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.TimeZone;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService; // Inject EmailService
    private final UserSubscriptionRepository userSubscriptionRepository;

    public String sendRegistrationOtp(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AlreadyExistsException("Email already exists.");
        }

        String otpCode = String.valueOf(100000 + new Random().nextInt(900000));

        // Save OTP to database
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setPurpose("REGISTER");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10)); 
        otp.setVerified(false);
        otpRepository.save(otp);

        // Send OTP via Email
        emailService.sendOtp(email, otpCode);

        return "OTP sent successfully to " + email;
    }


    public String registerUser(UserRegisterDTO dto, String otpCode) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email already exists.");
        }

        // Fetch the latest OTP for this email and purpose
        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(dto.getEmail(), "REGISTER")
                .orElseThrow(() -> new ResourceNotFoundException("OTP not found. Please request a new one."));

        // Validate OTP
        if (otp.getVerified()) {
            throw new BadRequestException("OTP has already been used.");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("OTP has expired.");
        }
        if (!otp.getOtpCode().equals(otpCode)) {
            throw new ForbiddenException("Invalid OTP.");
        }

        otp.setVerified(true);
        otpRepository.save(otp);

        Long count = userRepository.count() + 1;
        String userId = String.format("USER%05d", count);

        Users user = new Users();
        user.setId(userId);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setPrimaryRole(dto.getRole());
        user.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        user.setWakeUpTime(dto.getWakeUpTime());
        user.setCreatedAt(LocalDateTime.now());
        user.setPhoneNo(dto.getPhoneNo());
        user.setGuardianName(dto.getGuardianName());
        user.setGuardianPhoneNo(dto.getGuardianPhoneNo());

        userRepository.save(user);

        return "User created successfully";
    }


    public String sendForgetOtp(String email) {
        
        if (!userRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("User not found with this email");
        }

        otpRepository.findByEmailAndVerifiedFalse(email).ifPresent(existingOtp -> {
            existingOtp.setVerified(true);
            otpRepository.save(existingOtp);
        });

        String generatedOtp = String.format("%06d", new Random().nextInt(999999));
        
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(generatedOtp);
        otp.setPurpose("FORGET_PASSWORD");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10)); 
        otp.setVerified(false);
        otpRepository.save(otp);

        emailService.sendOtpEmail(email, generatedOtp);
        
        log.info("Forget Otp sent to email: {}", email);
        return "OTP sent to your email successfully.";
    }


	public String verifyOtp(VerifyOtpDTO dto) {
		
		Otp otp = otpRepository.findByEmailAndVerifiedFalseAndPurpose(dto.getEmail(), "FORGET_PASSWORD")
                .orElseThrow(() -> new BadRequestException("No active OTP found. Please request a new one."));
		
		log.info("db otp {}", otp.getOtpCode());
		log.info("DTO otp {}", dto.getOtp());
		
		if(!dto.getOtp().equals(otp.getOtpCode())) {
			throw new BadRequestException("Invalid OTP");
		}
		
		if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setVerified(true);
            otpRepository.save(otp);
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

		otp.setVerified(true);
        otpRepository.save(otp);
        
        log.info("Successfully otp verified");
        return "OTP Verified Successfully. You can now reset your password.";
	}


	public String resetpassword(ResetPasswordDTO resetPasswordDTO) {
		
		Users user = userRepository.findByEmail(resetPasswordDTO.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User Not found to reset new password"));
		
		user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));

		userRepository.save(user);
		
        return "Password reset successful! You can now login.";

	}


	public String changePassword(ChangePasswordDTO changePasswordDTO, String userId) {
		
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		
		if(!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
			throw new BadRequestException("Current password that you entered is incorrect.");
		}
		
		if(passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
			throw new BadRequestException("New password must be different from the current password.");
		}
		
		user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
		userRepository.save(user);

		log.info("password has been reset successfully");
		return "Password updated successfully for User: "+ user.getEmail();
	}


	public String deactivateAccount(String email) {
		
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User Not found to delete account"));
		
		user.setIs_active(false);
		userRepository.save(user);
		
		return "account deleted Succesfully";
	}
}