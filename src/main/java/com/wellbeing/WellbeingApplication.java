package com.wellbeing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WellbeingApplication {

    public static void main(String[] args) {
        SpringApplication.run(WellbeingApplication.class, args);
    }

}
