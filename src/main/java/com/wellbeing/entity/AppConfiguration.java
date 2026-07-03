package com.wellbeing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AppConfiguration {
    
    @Id
    private String configKey;   // Ex: "CONSULTATION_FEE"
    
    private String configValue; // Ex: "500"
    
    private String description;
}