package com.wellbeing.repository;

import com.wellbeing.entity.Tiplogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TipLogsRepository
        extends JpaRepository<Tiplogs, String> {

    List<Tiplogs> findByUserId(String userId);
    
    List<Tiplogs> findByUserIdAndAppliedAtBetweenOrderByAppliedAtDesc(
            String userId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

}