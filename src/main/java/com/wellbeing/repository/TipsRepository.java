package com.wellbeing.repository;

import com.wellbeing.entity.TipCategory;
import com.wellbeing.entity.Tips;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TipsRepository extends JpaRepository<Tips, String> {

	List<Tips> findByStatusAndTipCategory_Id(boolean status, Long categoryId);
}