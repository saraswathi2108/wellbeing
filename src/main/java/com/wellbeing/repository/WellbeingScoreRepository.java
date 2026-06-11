package com.wellbeing.repository;

import com.wellbeing.entity.Users;
import com.wellbeing.entity.WellbeingScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WellbeingScoreRepository extends JpaRepository<WellbeingScore, String> {

    Optional<WellbeingScore> findByUserId(String id);

	Optional<WellbeingScore> findByUser(Users user);
//jjj
}