package com.wellbeing.dto;

import com.wellbeing.entity.SleepDifficulty;
import com.wellbeing.entity.SleepDuration;
import lombok.Data;
import java.util.List;

@Data
public class ConsultationRequestDto {
    private String occupation;
    private String city;
    private String whatsappNumber;
    private List<SleepDifficulty> difficulties;
    private SleepDuration duration;
}