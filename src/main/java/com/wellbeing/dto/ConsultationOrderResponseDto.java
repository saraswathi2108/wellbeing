package com.wellbeing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationOrderResponseDto {
    private Long bookingId;
    private String razorpayOrderId;
    private Integer amount;
    private String keyId;
}