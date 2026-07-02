package com.wellbeing.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
public class Payment {

    @Id
    private String paymentId;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private Integer amount;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "user_sub_id")
    private UserSubscription userSubscription;

    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

}