package com.wellbeing.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class UserSubscription {

    @Id
    private String userSubId;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private UserSubscriptionStatus status;


    @ManyToOne
    @JoinColumn(name = "id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "sub_id")
    private Subscription subscription;

    @OneToMany(mappedBy = "userSubscription")
    private List<Payment> payments;

}
