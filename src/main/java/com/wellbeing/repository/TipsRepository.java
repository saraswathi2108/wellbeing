package com.wellbeing.repository;

import com.wellbeing.entity.Tips;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipsRepository extends JpaRepository<Tips, String> {
}