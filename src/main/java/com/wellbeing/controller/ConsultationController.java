package com.wellbeing.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.wellbeing.dto.ConsultationOrderResponseDto;
import com.wellbeing.dto.ConsultationRequestDto;
import com.wellbeing.dto.ConsultationVerifyDto;
import com.wellbeing.service.ConsultationService;
import com.wellbeing.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/consultation")
@RestController
@RequiredArgsConstructor
public class ConsultationController {
	
	
	private final ConsultationService consultationService;
	
	
	@PostMapping("/book")
	public ConsultationOrderResponseDto bookConsultation(@RequestBody ConsultationRequestDto dto,
														@AuthenticationPrincipal CustomUserDetails currentUser) throws RazorpayException {
		
		return consultationService.book(dto, currentUser.getId());
	}
	
	
	
	@PostMapping("/verify")
	public String verifyPaymentBooking(@RequestBody ConsultationVerifyDto consultationVerifyDto,
									@AuthenticationPrincipal CustomUserDetails currentUser) throws RazorpayException {
		
		return consultationService.verifyPayment(currentUser.getId(), consultationVerifyDto);
	}
	

}
