package com.wellbeing.controller;

import com.wellbeing.dto.ApplyTipRequest;
import com.wellbeing.dto.TipLogResponseDto;
import com.wellbeing.dto.WellbeingScoreResponse;
import com.wellbeing.entity.Tiplogs;
import com.wellbeing.service.WellBeingScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/tiplogs/{userId}")
    public List<TipLogResponseDto> getLogs(
            @PathVariable String userId) {

        return wellbeingScoreService.getUserTipLogs(userId);
    }


}
