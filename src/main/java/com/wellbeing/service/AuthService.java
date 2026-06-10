package com.wellbeing.service;

import com.wellbeing.dto.UserRegisterDTO;
import com.wellbeing.entity.Otp;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.OtpRepository;
import com.wellbeing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService; // Inject EmailService

    // Step 1: Method to generate, save, and send the OTP
    public String sendRegistrationOtp(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        // Generate a 6-digit OTP
        String otpCode = String.valueOf(100000 + new Random().nextInt(900000));

        // Save OTP to database
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setPurpose("REGISTER");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // Valid for 10 mins
        otp.setVerified(false);
        otpRepository.save(otp);

        // Send OTP via Email
        emailService.sendOtp(email, otpCode);

        return "OTP sent successfully to " + email;
    }

    // Step 2: Update registerUser to verify the OTP first
    public String registerUser(UserRegisterDTO dto, String otpCode) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        // Fetch the latest OTP for this email and purpose
        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(dto.getEmail(), "REGISTER")
                .orElseThrow(() -> new RuntimeException("OTP not found. Please request a new one."));

        // Validate OTP
        if (otp.getVerified()) {
            throw new RuntimeException("OTP has already been used.");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }
        if (!otp.getOtpCode().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP.");
        }

        // Mark OTP as verified so it can't be reused
        otp.setVerified(true);
        otpRepository.save(otp);

        // Proceed with user registration
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

        userRepository.save(user);
        return "User registered successfully";
    }
}