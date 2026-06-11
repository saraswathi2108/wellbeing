package com.wellbeing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.ActivityLogs;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, String> {

}
