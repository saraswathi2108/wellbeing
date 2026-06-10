package com.wellbeing.entity;


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
    private Boolean status;
    private Integer durationDays;

    @OneToMany(mappedBy = "subscription")
    private List<UserSubscription> userSubscriptions;

}
