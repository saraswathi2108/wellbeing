package com.wellbeing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String mailUsername;

    private final JavaMailSender mailSender;

    
    @Async
    public void sendOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mailUsername);
        message.setTo(to);
        message.setSubject("Your Well Being OTP Code");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);
    }
    

    
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP - Wellbeing ADMIN");
        message.setText("Your OTP for password reset is: " + otp + "\n\nThis OTP is valid for 10 minutes.");
        
        mailSender.send(message);
        log.info("OTP Email sent to {}", toEmail);
    }
}
    

//
//package com.wellbeing.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    @Value("${brevo.api.key}")
//    private String brevoApiKey;
//
//    @Value("${brevo.sender.email}")
//    private String senderEmail;
//
//    @Value("${brevo.sender.name}")
//    private String senderName;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    
//    
//    @Async
//    public void sendOtp(String to, String otp) {
//        String subject = "Your Well Being OTP Code";
//        String htmlContent = "<html><body><h2>Your OTP is: " + otp + "</h2><p>Please do not share this with anyone.</p></body></html>";
//        
//        sendEmailViaBrevo(to, subject, htmlContent);
//    }
//    
//    
//
//    @Async 
//    public void sendOtpEmail(String toEmail, String otp) {
//        String subject = "Password Reset OTP - Wellbeing ADMIN";
//        String htmlContent = "<html><body><h2>Your OTP for password reset is: " + otp + "</h2><p>This OTP is valid for 10 minutes.</p></body></html>";
//        
//        sendEmailViaBrevo(toEmail, subject, htmlContent);
//    }
//    
//    
//
//
//    private void sendEmailViaBrevo(String toEmail, String subject, String htmlContent) {
//    	
//        String brevoUrl = "https://api.brevo.com/v3/smtp/email";
//
//        
//        // 1. Set Headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("api-key", brevoApiKey);
//        headers.set("accept", "application/json");
//
//        // 2. Build the JSON Payload exactly how Brevo expects it
//        Map<String, Object> requestBody = Map.of(
//            "sender", Map.of("name", senderName, "email", senderEmail),
//            "to", List.of(Map.of("email", toEmail)),
//            "subject", subject,
//            "htmlContent", htmlContent
//        );
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        // 3. Make the API Call
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(brevoUrl, requestEntity, String.class);
//            log.info("Email successfully sent via Brevo to {}. MessageId: {}", toEmail, response.getBody());
//        } catch (Exception e) {
//            log.error("Failed to send email via Brevo to {}. Error: {}", toEmail, e.getMessage());
//        }
//    }
//}