package com.wellbeing.controller;

import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.dto.TipsResponseDto;
import com.wellbeing.entity.Tips;
import com.wellbeing.service.CustomUserDetails;
import com.wellbeing.service.TipsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/tips")
@RequiredArgsConstructor
public class TipsController {

    private final TipsService tipsService;
//    private final AiRecommendationService aiRecommendationService;

    @PostMapping("/add")
    public String createTip(
            @RequestBody TipsRequestDto dto,
            @RequestParam Long categoryId) {

        return tipsService.createTip(dto, categoryId);
    }

    @GetMapping("/byCategory")
    public List<TipsResponseDto> getTipsByCateId(@RequestParam Long categoryId,
    		@RequestParam (defaultValue = "true") boolean status) {
        return tipsService.getByCatId(categoryId, status);
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
    
//    @GetMapping("/ai-recommendations")
//    public String getAiRecommendations(@AuthenticationPrincipal CustomUserDetails currentUser) {
//        String userId = currentUser.getId();
//        
//        log.info("AI tips requested");
//        return aiRecommendationService.generateTipsForUser(userId);
//    }

}
