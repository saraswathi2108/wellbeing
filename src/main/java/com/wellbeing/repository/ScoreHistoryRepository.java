package com.wellbeing.repository;

import com.wellbeing.entity.ScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, String> {
}