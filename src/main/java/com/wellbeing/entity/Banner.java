package com.wellbeing.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class Banner {

    @Id
    private String bannerId;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT",length = 2000)
    private String description;
    
    private Boolean status;

    }

