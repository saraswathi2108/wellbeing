package com.wellbeing.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TipCategoryResponseDTO {
	
	private Long categoryId;
	private String categoryTipName;

}
