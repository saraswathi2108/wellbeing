package com.wellbeing.dto;

import lombok.Data;

@Data
public class SubscriptionRequest {

    private String subName;
    private String subDescription;
    private Integer price;
    private Integer durationDays;
}