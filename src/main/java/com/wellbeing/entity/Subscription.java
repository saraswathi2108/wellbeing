package com.wellbeing.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
@Data
@Entity
public class Subscription {

    @Id
    private String subId;
    private String subName;
    private String subDescription;
    private Integer price;
    private Boolean status;
    private String duration;

}
