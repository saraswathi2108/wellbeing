package com.wellbeing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Tiplogs {

    @Id
    private Long tipLogId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "tip_id")
    private Tips tips;

    private LocalDateTime appliedAt;
    private Integer scoreChange;

}
