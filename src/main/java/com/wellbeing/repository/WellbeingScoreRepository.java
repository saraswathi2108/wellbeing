package com.wellbeing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.Activities;
import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;

@Repository
public interface WellBeingScoreRepository extends JpaRepository<WellbeingScore, String> {

	Optional<WellbeingScore> findByUser(Users user);
	
    Optional<WellbeingScore> findByUserId(String id);


}
