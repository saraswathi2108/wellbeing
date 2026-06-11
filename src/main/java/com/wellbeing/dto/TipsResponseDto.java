package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TipsResponseDto {

    private String tipId;
    private String tipName;
    private String tipDescription;
    private Boolean status;
    private Integer tipScore;
}