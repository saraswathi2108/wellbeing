package com.wellbeing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellbeing.entity.AppConfiguration;
import com.wellbeing.entity.Users;

@Repository
public interface AppConfigurationRepo extends JpaRepository<AppConfiguration, String> {

	Optional<AppConfiguration> findByConfigKey(String string);

}
