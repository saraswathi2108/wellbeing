package com.wellbeing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;

public interface WellBeingScoreRepository extends JpaRepository<WellbeingScore, String> {
	
    Optional<WellbeingScore> findByUserId(String id);

	Optional<WellbeingScore> findByUser(Users user);

}
