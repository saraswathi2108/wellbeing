package com.wellbeing.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import com.wellbeing.dto.TipResponse;
import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import com.wellbeing.repository.UserRepository;
import com.wellbeing.repository.WellBeingScoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final WellBeingScoreRepository wellBeingScoreRepository;

    public List<TipResponse> askAI(String userId){
    	
    	Users user = userRepository.findById(userId)
    			.orElseThrow(() -> new ResourceNotFoundException("User Not Found to generate AI Tips"));
    	
    	WellbeingScore wellbeingScore = wellBeingScoreRepository.findByUser(user)
    			.orElseThrow(() -> new ResourceNotFoundException("User wellbeing score not found"));
    	
    	int currentScore = wellbeingScore.getCurrentScore() != null ? wellbeingScore.getCurrentScore() : 50;
    	
    	String scoreStatus;

    	if(currentScore <=30){

    	scoreStatus="Low";

    	}else if(currentScore<75){

    	scoreStatus="Moderate";

    	}else{

    	scoreStatus="High";

    	}
    	
    	String systemPrompt = """
    			You are an expert wellbeing coach.

    			Always provide practical wellbeing advice based user current wellbeing score and tip score should be round figure (10-30).


				""";
    	
    	
    	String userPrompt = String.format("""
    			Age : %d

    			Gender : %s

    			Wellbeing Score : %d
    			
    			scoreStatus : %s
    			
    			The user is struggling and needs gentle recovery activities.

    			Generate 3 wellbeing tips.
    			""",
    			user.getAge(),
    			user.getGender(),
    			currentScore,
    			scoreStatus);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(new ParameterizedTypeReference<List<TipResponse>>() {
				});

    }

}