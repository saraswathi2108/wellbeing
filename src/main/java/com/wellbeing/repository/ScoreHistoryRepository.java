package com.wellbeing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.ScoreHistory;


@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, String>{

}
