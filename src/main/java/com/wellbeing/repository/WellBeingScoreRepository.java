package com.wellbeing.repository;

import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WellBeingScoreRepository extends JpaRepository<WellbeingScore,String> {
    Optional<WellbeingScore> findByUser(Users user);

    Optional<WellbeingScore> findByUserId(String userId);
}
