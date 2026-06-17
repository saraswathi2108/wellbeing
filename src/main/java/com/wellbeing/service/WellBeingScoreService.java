package com.wellbeing.service;

import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.ApplyTipRequest;
import com.wellbeing.dto.TipLogResponseDto;
import com.wellbeing.dto.WellbeingScoreResponse;
import com.wellbeing.entity.ScoreHistory;
import com.wellbeing.entity.Tiplogs;
import com.wellbeing.entity.Tips;
import com.wellbeing.entity.WellbeingScore;
import com.wellbeing.repository.ScoreHistoryRepository;
import com.wellbeing.repository.TipLogsRepository;
import com.wellbeing.repository.TipsRepository;
import com.wellbeing.repository.WellBeingScoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequestMapping("/api/wellbeingscore")
@RequiredArgsConstructor
public class WellBeingScoreService {

    private final WellBeingScoreRepository wellbeingScoreRepository;
    private final TipsRepository tipsRepository;
    private final ScoreHistoryRepository scoreHistoryRepository;
    private final TipLogsRepository tipLogsRepository;


    public WellbeingScoreResponse getCurrentScore(String userId) {

        WellbeingScore score = wellbeingScoreRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wellbeing score not found for user: " + userId));

        return WellbeingScoreResponse.builder()
                .userId(score.getUser().getId())
                .wellScoreId(score.getWellScoreId())
                .currentScore(score.getCurrentScore())
                .updatedAt(score.getUpdatedAt())
                .build();
    }

    @Transactional
    public String applyTip(ApplyTipRequest request) {

        Tips tip = tipsRepository.findById(request.getTipId())
                .orElseThrow(() -> new ResourceNotFoundException("Tip not found"));

        WellbeingScore score = wellbeingScoreRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Score not found"));

        Integer currentScore =
                score.getCurrentScore() == null ? 0 : score.getCurrentScore();

        Integer updatedScore =
                currentScore + tip.getTipScore();

        score.setCurrentScore(updatedScore);
        score.setUpdatedAt(LocalDateTime.now());

        wellbeingScoreRepository.save(score);

        Tiplogs log = new Tiplogs();

        log.setTipLogId(UUID.randomUUID().toString());
        log.setUser(score.getUser());
        log.setTip(tip);
        log.setScoreChange(tip.getTipScore());
        log.setAppliedAt(LocalDateTime.now());

        tipLogsRepository.save(log);

        return "Tip Applied Successfully";
    }

    public List<TipLogResponseDto> getUserTipLogs(String userId) {

        return tipLogsRepository.findByUserId(userId)
                .stream()
                .map(log -> TipLogResponseDto.builder()
                        .tipLogId(log.getTipLogId())
                        .tipId(log.getTip().getTipId())
                        .tipName(log.getTip().getTipName())
                        .scoreChange(log.getScoreChange())
                        .appliedAt(log.getAppliedAt())
                        .build())
                .toList();
    }




}
