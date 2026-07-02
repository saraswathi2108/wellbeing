package com.wellbeing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wellbeing.ExceptionHandler.BadRequestException;
import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.TipCategoryResponseDTO;
import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.dto.TipsResponseDto;
import com.wellbeing.entity.TipCategory;
import com.wellbeing.repository.CategoryTipRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryTipService {
	
	
	private final CategoryTipRepo categoryTipRepo;

	public String addCategory(TipsRequestDto tipsRequestDto) {
		
		if(categoryTipRepo.existsByCategoryName(tipsRequestDto.getTipName())) {
			throw new BadRequestException("Category already exist with name: "+ tipsRequestDto.getTipName());
		}
		
		TipCategory tipCategory = new TipCategory();
		tipCategory.setCategoryName(tipsRequestDto.getTipName());
		tipCategory.setStatus(true);
		
		categoryTipRepo.save(tipCategory);
		
		return "Tip Category created Succesfully";

	}

	public List<TipCategoryResponseDTO> getAllTipsCate(boolean status) {
		
		List<TipCategory> tipCategories = categoryTipRepo.findByStatus(status);
		
		if(tipCategories.isEmpty()) {
			throw new ResourceNotFoundException("TipCategory Not found By status: "+ status);
		}
		
//		return tipCategories.stream()
//				.map(tipCategoriey -> TipCategoryResponseDTO.builder()
//						.categoryId(tipCategoriey.getId())
//						.categoryTipName(tipCategoriey.getCategoryName())
//						.build())
//				.toList();
		
		return tipCategories.stream()
				.map(tipCategorie -> {
					TipCategoryResponseDTO dto = new TipCategoryResponseDTO();
					dto.setCategoryId(tipCategorie.getId());
					dto.setCategoryTipName(tipCategorie.getCategoryName());
					
					return dto;
				})
				.toList();

	}

}
