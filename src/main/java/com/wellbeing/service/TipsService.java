package com.wellbeing.service;

import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.dto.TipsResponseDto;
import com.wellbeing.entity.TipCategory;
import com.wellbeing.entity.Tips;
import com.wellbeing.repository.CategoryTipRepo;
import com.wellbeing.repository.TipsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipsService {

    private final TipsRepository tipsRepository;
    private final CategoryTipRepo categoryTipRepo;

    public String createTip(TipsRequestDto dto, Long categoryTipId) {
    	
    	TipCategory tipCategory = categoryTipRepo.findById(categoryTipId)
    			.orElseThrow(() -> new ResourceNotFoundException("CategoryTip not found to add Tip"));

        Tips tip = new Tips();
        
        Long count = tipsRepository.count() + 1;
		String tipId = String.format("TIPS%05d", count);

		tip.setTipId(tipId);
        tip.setTipName(dto.getTipName());
        tip.setTipDescription(dto.getTipDescription());
        tip.setStatus(true);
        tip.setTipCategory(tipCategory);
        tip.setTipScore(dto.getTipScore());

        Tips savedTip = tipsRepository.save(tip);

        return "Tip added Succesfully";
    }

    public List<TipsResponseDto> getByCatId(Long categoryId, boolean status) {

        return tipsRepository.findByStatusAndTipCategory_Id(status, categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TipsResponseDto getTipById(String tipId) {

        Tips tip = tipsRepository.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Tip not found"));

        return mapToResponse(tip);
    }

    public TipsResponseDto updateTip(
            String tipId,
            TipsRequestDto dto) {

        Tips tip = tipsRepository.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Tip not found"));

        tip.setTipName(dto.getTipName());
        tip.setTipDescription(dto.getTipDescription());
        tip.setStatus(dto.getStatus());
        tip.setTipScore(dto.getTipScore());

        Tips updatedTip = tipsRepository.save(tip);

        return mapToResponse(updatedTip);
    }

    public String deleteTip(String tipId) {

        Tips tip = tipsRepository.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Tip not found"));

        tipsRepository.delete(tip);

        return "Tip deleted successfully";
    }

    private TipsResponseDto mapToResponse(Tips tip) {
        return TipsResponseDto.builder()
                .tipId(tip.getTipId())
                .tipName(tip.getTipName())
                .tipDescription(tip.getTipDescription())
                .status(tip.getStatus())
                .tipScore(tip.getTipScore())
                .categoryId(tip.getTipCategory() != null ? tip.getTipCategory().getId() : null)
                .categoryName(tip.getTipCategory() != null ? tip.getTipCategory().getCategoryName() : null)
                .build();
    }
}