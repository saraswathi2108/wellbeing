package com.wellbeing.repository;

import com.wellbeing.entity.Users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<String, Users> {
	
	Optional<Users> findByEmail(String email);
	
	boolean existsByEmail(String email);

}
