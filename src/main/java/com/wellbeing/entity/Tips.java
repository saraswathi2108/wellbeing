package com.wellbeing.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Tips {


    @Id
    private String tipId;
    private String tipName;
    
    @Column(columnDefinition = "TEXT")
    private String tipDescription;
    private Boolean status;
    private Integer tipScore;

}
