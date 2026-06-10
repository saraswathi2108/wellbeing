package com.wellbeing.dto;

import com.wellbeing.entity.PrimaryRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
public class UserRegisterDTO {
    private String name;
    private String email;
    private String password;
    private Integer age;
    private String gender;
    private PrimaryRole role;
    private  String Others;
    private LocalDateTime wakeUpTime;
}