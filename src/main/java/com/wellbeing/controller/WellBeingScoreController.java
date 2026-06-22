package com.wellbeing.controller;

import com.wellbeing.dto.ApplyTipRequest;
import com.wellbeing.dto.TipLogResponseDto;
import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.dto.WellbeingScoreResponse;
import com.wellbeing.entity.Tiplogs;
import com.wellbeing.service.CustomUserDetails;
import com.wellbeing.service.WellBeingScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WellBeingScoreController {
    private final WellBeingScoreService wellbeingScoreService;

    @GetMapping("/user/{userId}")
    public WellbeingScoreResponse getCurrentScore(@PathVariable String userId) {
        return wellbeingScoreService.getCurrentScore(userId);
    }

    @PostMapping("/applytip")
    public ResponseEntity<String> applyTip(
            @RequestBody ApplyTipRequest request) {

        return ResponseEntity.ok(
                wellbeingScoreService.applyTip(request));
    }

    @GetMapping("/tiplogs")
    public List<TipLogResponseDto> getLogs(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

    	String userId = currentUser.getId();
        return wellbeingScoreService.getUserTipLogs(userId);
    }

    
    
    @PostMapping("/saveAndApplyTip")
    public String saveAndApplyTip(@RequestBody TipsRequestDto tipsRequestDto,
    							@AuthenticationPrincipal CustomUserDetails currentUser) {
    	
    	String userId = currentUser.getId();
    	return wellbeingScoreService.saveAndApplyAiTip(tipsRequestDto, userId);
    }

}
