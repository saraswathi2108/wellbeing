package com.wellbeing.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserDeletedActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activities activity;
}