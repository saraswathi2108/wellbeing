package com.wellbeing.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wellbeing.dto.TipResponse;
import com.wellbeing.service.AIService;
import com.wellbeing.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    @GetMapping
    public List<TipResponse> ask(@AuthenticationPrincipal CustomUserDetails currentUser){

    	String userId =  currentUser.getId();
    	
    	log.info("AI request");
        return aiService.askAI(userId);

    }

}