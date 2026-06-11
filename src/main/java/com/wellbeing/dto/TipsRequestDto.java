package com.wellbeing.dto;

import lombok.Data;

@Data
public class TipsRequestDto {

    private String tipName;
    private String tipDescription;
    private Boolean status;
    private Integer tipScore;
}