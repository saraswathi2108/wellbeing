package com.wellbeing.repository;

import com.wellbeing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<Long, User> {
}
