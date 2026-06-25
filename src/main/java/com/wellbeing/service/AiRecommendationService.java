//package com.wellbeing.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.wellbeing.entity.Users;
//import com.wellbeing.entity.WellbeingScore;
//import com.wellbeing.repository.UserRepository;
//import com.wellbeing.repository.WellBeingScoreRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AiRecommendationService {
//
//    private final UserRepository userRepository;
//    private final WellBeingScoreRepository wellBeingScoreRepository;
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.api.url}")
//    private String apiUrl;
//
//    public String generateTipsForUser(String userId) { // Return type is now String
//        // 1. Fetch User Context
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        WellbeingScore scoreObj = wellBeingScoreRepository.findByUser(user)
//                .orElse(null);
//        
//        int currentScore = (scoreObj != null && scoreObj.getCurrentScore() != null) ? scoreObj.getCurrentScore() : 50;
//
//        // 2. Determine Category and Tone
//        String category;
//        String toneInstruction;
//
//        if (currentScore <= 30) {
//            category = "Strained";
//            toneInstruction = "The user's wellbeing is very low. Be highly empathetic. Provide 3 very gentle, easy-to-do recovery tips to help them relieve stress, feel better immediately.";
//        } else if (currentScore < 75) { 
//            category = "Balanced";
//            toneInstruction = "The user is doing okay, but has room for improvement. Provide 3 practical and actionable tips to help them elevate their daily routine and boost their score.";
//        } else {
//            category = "Flourishing";
//            toneInstruction = "The user is doing incredibly well! Start the first tip by strongly appreciating their consistency. Provide 3 advanced tips to help them maintain this peak state.";
//        }
//
//        // 3. Build the Strict Dynamic Prompt for Array of Objects
//        String prompt = String.format(
//            "Act as a professional wellbeing coach. The user is a %d-year-old %s. " +
//            "Their current wellbeing score is %d out of 100, which falls into the '%s' category. " +
//            "%s " +
//            "Do not use markdown formatting. Return ONLY a valid JSON array containing exactly 3 objects. " +
//            "Each object MUST have exactly these three keys: 'tipName' (a short title), 'tipDescription' (actionable advice), and 'tipScore' (an integer score should be based on tipDescription and score should be round figure ex:10,15,20). " +
//            "Example format: [{\"tipName\": \"Drink Water\", \"tipDescription\": \"Drink 2 glasses now\", \"tipScore\": 10}]",
//            user.getAge(), user.getGender(), currentScore, category, toneInstruction
//        );
//        
//        try {
//            // 4. Prepare Gemini API Request Body properly
//            ObjectMapper mapper = new ObjectMapper();
//            
//            Map<String, Object> requestBodyMap = Map.of(
//                "contents", List.of(
//                    Map.of("parts", List.of(
//                        Map.of("text", prompt)
//                    ))
//                )
//            );
//            
//            String requestBody = mapper.writeValueAsString(requestBodyMap);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//            // 5. Make the Call
//            String fullUrl = apiUrl + "?key=" + apiKey;
//            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, requestEntity, String.class);
//            
//            // 6. Extract raw text from Gemini's JSON structure
//            String responseBody = response.getBody();
//            JsonNode root = mapper.readTree(responseBody);
//            String textResponse = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
//            
//            // Clean markdown blocks if Gemini stubbornly adds them
//            textResponse = textResponse.replace("```json", "").replace("```", "").trim();
//            
//            // Return raw JSON string directly to frontend
//            return textResponse;
//            
//        } catch (Exception e) {
//            log.error("AI API Call failed", e);
//            // Fallback valid JSON array so frontend parsing doesn't break
//            return "[{\"tipName\":\"Drink Water\",\"tipDescription\":\"Drink a glass of water\",\"tipScore\":5}," +
//                   "{\"tipName\":\"Take a Walk\",\"tipDescription\":\"Walk for 10 minutes\",\"tipScore\":10}," +
//                   "{\"tipName\":\"Rest\",\"tipDescription\":\"Get 8 hours of sleep\",\"tipScore\":15}]";
//        }
//    }
//    
//    
//    
//
////    private List<String> parseGeminiResponse(String responseBody) {
////        List<String> tips = new ArrayList<>();
////        try {
////            ObjectMapper mapper = new ObjectMapper();
////            JsonNode root = mapper.readTree(responseBody);
////            // Extracting the text from Gemini's nested JSON response
////            String textResponse = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
////            
////            // Clean up the JSON string if Gemini adds markdown code blocks
////            textResponse = textResponse.replace("```json", "").replace("```", "").trim();
////            
////            JsonNode tipsArray = mapper.readTree(textResponse);
////            if (tipsArray.isArray()) {
////                for (JsonNode node : tipsArray) {
////                    tips.add(node.asText());
////                }
////            }
////        } catch (Exception e) {
////            log.error("Failed to parse AI response", e);
////            tips.add("Focus on maintaining a balanced diet and regular exercise.");
////        }
////        return tips;
////    }
//}