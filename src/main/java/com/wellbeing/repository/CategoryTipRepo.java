package com.wellbeing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.TipCategory;

@Repository
public interface CategoryTipRepo extends JpaRepository<TipCategory, Long> {

	boolean existsByCategoryName(String tipName);

	List<TipCategory> findByStatus(boolean status);

}
