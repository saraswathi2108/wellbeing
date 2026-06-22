package com.wellbeing.service;

import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.ApplyTipRequest;
import com.wellbeing.dto.TipLogResponseDto;
import com.wellbeing.dto.TipsRequestDto;
import com.wellbeing.dto.WellbeingScoreResponse;
import com.wellbeing.entity.ScoreHistory;
import com.wellbeing.entity.Tiplogs;
import com.wellbeing.entity.Tips;
import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import com.wellbeing.repository.ScoreHistoryRepository;
import com.wellbeing.repository.TipLogsRepository;
import com.wellbeing.repository.TipsRepository;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.WellBeingScoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;


@Service
@RequestMapping("/api/wellbeingscore")
@RequiredArgsConstructor
@Slf4j
public class WellBeingScoreService {

    private final WellBeingScoreRepository wellbeingScoreRepository;
    private final TipsRepository tipsRepository;
    private final ScoreHistoryRepository scoreHistoryRepository;
    private final TipLogsRepository tipLogsRepository;
    private final UserRepository userRepository;


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
    	
    	ZoneId istZone = ZoneId.of("Asia/Kolkata");
	    LocalDateTime startOfDay = LocalDate.now(istZone).atStartOfDay();
	    LocalDateTime endOfDay = LocalDate.now(istZone).atTime(LocalTime.MAX);

        return tipLogsRepository.findByUserIdAndAppliedAtBetweenOrderByAppliedAtDesc(userId, startOfDay, endOfDay)
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

    
    
    
    @Transactional
    public String saveAndApplyAiTip(TipsRequestDto tipDto, String userId) {

    	Users user = userRepository.findById(userId)
    			.orElseThrow(() -> new ResourceNotFoundException("User Not found to apply tip"));
        
        // 1. Save the AI generated Tip to Tips table
        Tips tip = new Tips();
        tip.setTipId("TIP" + System.currentTimeMillis()); 
        tip.setTipName(tipDto.getTipName());
        tip.setTipDescription(tipDto.getTipDescription());
        tip.setStatus(true);
        tip.setTipScore(tipDto.getTipScore());
        
        Tips savedTip = tipsRepository.save(tip);

        // 2. Fetch User Wellbeing Score
        WellbeingScore score = wellbeingScoreRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found for user"));

        // 3. Update the Score
        Integer currentScore = score.getCurrentScore() == null ? 0 : score.getCurrentScore();
        Integer updatedScore = currentScore + savedTip.getTipScore();
        
        // Safe check
        updatedScore = Math.min(100, updatedScore);

        score.setCurrentScore(updatedScore);
        score.setUpdatedAt(LocalDateTime.now());
        wellbeingScoreRepository.save(score);

        // 4. Log the action in Tiplogs table
        Tiplogs log2 = new Tiplogs();
        log2.setTipLogId(UUID.randomUUID().toString());
        log2.setUser(score.getUser());
        log2.setTip(savedTip);
        log2.setScoreChange(savedTip.getTipScore());
        log2.setAppliedAt(LocalDateTime.now());

        tipLogsRepository.save(log2);
        
        Long count = scoreHistoryRepository.count() + 1;
		String scoreHistoryId = String.format("SCOREHIS%05d", count);
        
        ScoreHistory scoreHistory = new ScoreHistory();
        scoreHistory.setScoreHistoryId(scoreHistoryId);
        scoreHistory.setNewScore(updatedScore);
        scoreHistory.setPreviousScore(score.getCurrentScore());
        scoreHistory.setRecordedAt(LocalDateTime.now());
        scoreHistory.setTips(savedTip);
        scoreHistory.setUser(user);
        scoreHistory.setWellbeingScore(score);
        
        
        scoreHistoryRepository.save(scoreHistory);
        
        log.info("AI tip saved in DB Wellbeing score updated, TipLogs and Score Hostory saved");
                
        return "AI Tip Saved and Applied Successfully. New Score: " + updatedScore;
    }



}
