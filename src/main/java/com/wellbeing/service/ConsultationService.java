package com.wellbeing.service;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.wellbeing.ExceptionHandler.BadRequestException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.ExceptionHandler.UnauthorizedException;
import com.wellbeing.dto.ConsultationOrderResponseDto;
import com.wellbeing.dto.ConsultationRequestDto;
import com.wellbeing.dto.ConsultationVerifyDto;
import com.wellbeing.entity.ConsultationBooking;
import com.wellbeing.entity.PaymentStatus;
import com.wellbeing.entity.Users;
import com.wellbeing.repository.AppConfigurationRepo;
import com.wellbeing.repository.ConsultationBookingRepo;
import com.wellbeing.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationService {
	
	private final UserRepository userRepository;
	private final RazorpayClient razorpayClient;
	private final ConsultationBookingRepo consultationBookingRepo;
	private final AppConfigurationRepo appConfigurationRepo;
	
	
	
	@Value("${razor.key.id}")
	private String keyId;
	
	@Value("${razor.key.secret}")
	private String secretKey;
	
	
	
	private Integer getDynamicConsultationFee() {
        return appConfigurationRepo.findById("CONSULTATION_FEE")
                .map(config -> Integer.parseInt(config.getConfigValue()))
                .orElse(500);
    }
	

	
	@Transactional
	public ConsultationOrderResponseDto book(ConsultationRequestDto dto, String userId) throws RazorpayException {
		
		Users users = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found with Id: "+ userId));
		
		Integer dynamicFee = getDynamicConsultationFee();
		
		JSONObject options = new JSONObject();
        options.put("amount", dynamicFee * 100);
        options.put("currency", "INR");
        options.put("receipt", "CONS_" + userId + "_" + System.currentTimeMillis());
		
        
        Order order = razorpayClient.orders.create(options);
        
        ConsultationBooking booking = new ConsultationBooking();
        booking.setUser(users);
        booking.setOccupation(dto.getOccupation());        
        booking.setCity(dto.getCity());
        
        booking.setWhatsappNumber(dto.getWhatsappNumber());
        booking.setDifficulties(dto.getDifficulties());
        booking.setDuration(dto.getDuration());
        
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setRazorpayOrderId(order.get("id"));
        booking.setAmount(dynamicFee);
        booking.setCreatedAt(LocalDateTime.now());
        
        ConsultationBooking savedBooking = consultationBookingRepo.save(booking);
        
		return ConsultationOrderResponseDto.builder()
				.bookingId(savedBooking.getId())
				.razorpayOrderId(savedBooking.getRazorpayOrderId())
				.amount(dynamicFee)
				.keyId(keyId)
				.build();
	}


	public String verifyPayment(String id, ConsultationVerifyDto dto) throws RazorpayException {
		
		Users users = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found: "+ id));
		
		ConsultationBooking booking = consultationBookingRepo.findById(dto.getBookingId())
				.orElseThrow(() -> new ResourceNotFoundException("Consultation booking Not found with id: "+ dto.getBookingId()));
				
		if(!booking.getUser().getId().equals(id)) {
			throw new UnauthorizedException("This booking does not belong to you");
		}
				
		
		boolean verifySignature = Utils.verifySignature(
				dto.getRazorpayOrderId()+ "|" + dto.getRazorpayPaymentId(),
				dto.getRazorpaySignature(),
				secretKey);

		if (!verifySignature) {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            consultationBookingRepo.save(booking);
            throw new BadRequestException("Invalid payment signature");
        }



		booking.setRazorpayPaymentId(dto.getRazorpayPaymentId());
        booking.setRazorpaySignature(dto.getRazorpaySignature());
        booking.setPaymentStatus(PaymentStatus.SUCCESSFUL);
        
        consultationBookingRepo.save(booking);

        return "Consultation booked successfully";
	}

}
