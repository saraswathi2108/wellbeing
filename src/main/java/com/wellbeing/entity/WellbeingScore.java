package com.wellbeing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data

public class WellbeingScore {

    @Id
    private String wellScoreId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private Integer currentScore;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "wellbeingScore", cascade = CascadeType.ALL)
    private List<ScoreHistory> scoreHistories;
}