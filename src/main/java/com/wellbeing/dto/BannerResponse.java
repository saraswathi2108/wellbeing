package com.wellbeing.dto;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class BannerResponse {

    private String bannerId;
    private String name;
    private String description;


}
