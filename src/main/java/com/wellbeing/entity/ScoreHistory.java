package com.wellbeing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ScoreHistory {

    @Id
    private String scoreHistoryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private Integer tipId;

    private Integer previousScore;

    private Integer newScore;

    @ManyToOne
    @JoinColumn(name = "well_score_id")
    private WellbeingScore wellbeingScore;

    private LocalDateTime recordedAt;
}
