package com.wellbeing.repository;

import com.wellbeing.dto.BannerResponse;
import com.wellbeing.entity.Banner;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner,String> {

	Collection<Banner> findByStatus(Boolean status);
}
