package com.wellbeing.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class User_Subscription {

    @Id
    private String userSubId;
    private Date startDate;
    private Date endDate;
    private Boolean status;

    private String userId;


    private String subId;

}
