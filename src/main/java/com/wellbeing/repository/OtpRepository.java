package com.wellbeing.repository;

import com.wellbeing.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email,
            String purpose
    );


    Optional<Otp> findTopByEmailOrderByCreatedAtDesc(String email);
}