package com.wellbeing.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Subscription {

    @Id
    private String subId;
    private String subName;
    private String subDescription;
    private Integer price;
    
    @Column(columnDefinition = "integer default 0")
    private Integer discountPercentage; 
    
    private Integer finalPrice;
    
    private Boolean status;
    private Integer durationDays;
    @Column(nullable = false)
    private Boolean trialPlan;

    @OneToMany(mappedBy = "subscription")
    private List<UserSubscription> userSubscriptions;

}
