package com.wellbeing.dto;

import lombok.Data;

@Data
public class ConsultationVerifyDto {
	
	private Long bookingId;
    private String razorpayOrderId;
	private String razorpaySignature;
	private String razorpayPaymentId;

}
