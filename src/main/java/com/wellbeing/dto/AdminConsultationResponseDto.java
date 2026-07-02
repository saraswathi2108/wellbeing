package com.wellbeing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminConsultationResponseDto {
    private Long bookingId;
    private String userId;
    private String userName;
    private Integer userAge;
    private String userGender;
    private String registeredPhone; 
    

    private String whatsappNumber; 
    private String occupation;
    private String city;
    private List<String> difficulties; 
    private String duration;
    
    private Integer amountPaid;
    private LocalDateTime bookedAt;
    private Boolean adminInteracted;
}