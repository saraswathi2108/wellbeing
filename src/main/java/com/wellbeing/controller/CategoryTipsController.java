package com.wellbeing.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wellbeing.dto.TipCategoryResponseDTO;
import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.service.CategoryTipService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/tipCategory")
@RestController
public class CategoryTipsController {
	
	
	private final CategoryTipService categoryTipService;
	
	
	@PostMapping("/add")
	public String addCategory(@RequestBody TipsRequestDto tipsRequestDto) {
		
		return categoryTipService.addCategory(tipsRequestDto);
	}
	
	
	@GetMapping("/getByStatus")
	public List<TipCategoryResponseDTO> getByStatus(@RequestParam (defaultValue = "true") boolean status){
		
		return categoryTipService.getAllTipsCate(status);
	}

}
