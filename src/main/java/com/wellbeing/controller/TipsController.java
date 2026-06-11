package com.wellbeing.controller;

import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.dto.TipsResponseDto;
import com.wellbeing.entity.Tips;
import com.wellbeing.service.TipsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tips")
@RequiredArgsConstructor
public class TipsController {

    private final TipsService tipsService;

    @PostMapping
    public TipsResponseDto createTip(
            @RequestBody TipsRequestDto dto) {

        return tipsService.createTip(dto);
    }

    @GetMapping
    public List<TipsResponseDto> getAllTips() {
        return tipsService.getAllTips();
    }

    @GetMapping("/{tipId}")
    public TipsResponseDto getTipById(
            @PathVariable String tipId) {

        return tipsService.getTipById(tipId);
    }

    @PutMapping("/{tipId}")
    public TipsResponseDto updateTip(
            @PathVariable String tipId,
            @RequestBody TipsRequestDto dto) {

        return tipsService.updateTip(tipId, dto);
    }

    @DeleteMapping("/{tipId}")
    public String deleteTip(
            @PathVariable String tipId) {

        return tipsService.deleteTip(tipId);
    }

}
