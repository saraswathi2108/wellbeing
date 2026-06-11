package com.wellbeing.service;

import com.wellbeing.dto.UserRegisterDTO;
import com.wellbeing.entity.Otp;
import com.wellbeing.entity.UserSubscription;
import com.wellbeing.entity.UserSubscriptionStatus;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.OtpRepository;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
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
    private final UserSubscriptionRepository userSubscriptionRepository;

    public String sendRegistrationOtp(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists.");
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

        Users savedUser = userRepository.save(user);
        UserSubscription userSubscription =
                new UserSubscription();

        Long count1 =
                userSubscriptionRepository.count() + 1;

        String userSubId =
                String.format("US%03d", count1);

        userSubscription.setUserSubId(userSubId);

        userSubscription.setUser(savedUser);

        userSubscription.setStatus(
                UserSubscriptionStatus.TRIAL);

        userSubscription.setStartDate(LocalDate.now());

        userSubscription.setEndDate(LocalDate.now().plusDays(7));

        userSubscription.setSubscription(null);

        userSubscriptionRepository.save(userSubscription);
        return "user is created successfully";

    }
}