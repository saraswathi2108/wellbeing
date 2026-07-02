package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SubscriptionAnalyticsDto {
    private String subId;
    private String subName;
    private Integer price;
    private Long totalPurchases;
    private List<String> userIds;
}