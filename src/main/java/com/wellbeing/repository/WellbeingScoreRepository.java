package com.wellbeing.repository;

import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;

@Repository
public interface WellbeingScoreRepository extends JpaRepository<WellbeingScore, String> {

    Optional<WellbeingScore> findByUserId(String id);

	Optional<WellbeingScore> findByUser(Users user);
}