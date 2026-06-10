package com.wellbeing.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
public class Payment {

    @Id
    private String paymentId;

    private String razorpayOrderId;

    private Double amount;


    private String paymentMethod;

    private String paymentStatus;

    private LocalDateTime paymentDate;



    @ManyToOne
    @JoinColumn(name = "user_sub_id")
    private UserSubscription userSubscription;
}