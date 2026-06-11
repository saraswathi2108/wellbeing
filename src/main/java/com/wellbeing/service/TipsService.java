package com.wellbeing.service;

import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.dto.TipsResponseDto;
import com.wellbeing.entity.Tips;
import com.wellbeing.repository.TipsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipsService {

    private final TipsRepository tipsRepository;

    public TipsResponseDto createTip(TipsRequestDto dto) {

        Tips tip = new Tips();

        tip.setTipId("TIP" + System.currentTimeMillis());
        tip.setTipName(dto.getTipName());
        tip.setTipDescription(dto.getTipDescription());
        tip.setStatus(dto.getStatus());
        tip.setTipScore(dto.getTipScore());

        Tips savedTip = tipsRepository.save(tip);

        return mapToResponse(savedTip);
    }

    public List<TipsResponseDto> getAllTips() {

        return tipsRepository.findAll()
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
                .build();
    }
}