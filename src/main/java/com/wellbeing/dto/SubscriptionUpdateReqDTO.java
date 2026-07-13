package com.wellbeing.dto;

import lombok.Data;

@Data
public class SubscriptionUpdateReqDTO {
	
    private String subName;
    private String subDescription;
    private Integer price;
    private Integer discountPercentage;

    private Integer durationDays;

}
