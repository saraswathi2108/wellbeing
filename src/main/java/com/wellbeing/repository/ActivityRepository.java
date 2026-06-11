package com.wellbeing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.Activities;

@Repository
public interface ActivityRepository extends JpaRepository<Activities, String> {

	Optional<Activities> findByActivityName(String activityName);

}
