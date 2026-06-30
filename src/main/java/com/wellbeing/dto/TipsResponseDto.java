package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class TipsResponseDto {
    private String tipId;
    private String tipName;
    private String tipDescription;
    private Boolean status;
    private Integer tipScore;
    
    private Long categoryId;
    private String categoryName;
}