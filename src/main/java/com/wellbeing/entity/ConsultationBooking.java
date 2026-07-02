package com.wellbeing.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;



@Data
@Entity
public class ConsultationBooking {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
	
	private String occupation;
    private String city;
    private String whatsappNumber;
    
    
    @ElementCollection(targetClass = SleepDifficulty.class)
    @CollectionTable(name = "booking_difficulties", joinColumns = @JoinColumn(name = "booking_id"))
    @Enumerated(EnumType.STRING) 
    @Column(name = "difficulty")
    private List<SleepDifficulty> difficulties;
    
    @Enumerated(EnumType.STRING)
    private SleepDuration duration;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    
    private Integer amount;
    private LocalDateTime createdAt;
    
    private Boolean adminInteracted = false;

}
