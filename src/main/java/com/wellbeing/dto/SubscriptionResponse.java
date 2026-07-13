package com.wellbeing.dto;


import lombok.Data;

@Data
public class SubscriptionResponse {

    private String subId;
    private String subName;
    private String subDescription;
    
    private Integer discountAmount; 
    private Integer finalPrice;
    private Integer price;
    
    private Integer durationDays;
    private Boolean status;
}
