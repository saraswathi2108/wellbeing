package com.wellbeing.dto;


import lombok.Data;

@Data
public class SubscriptionResponse {

    private String subId;
    private String subName;
    private String subDescription;
    private Integer price;
    private Integer durationDays;
    private Boolean status;
}
