package com.wellbeing.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String identifier;
    private String name;
    private String password;
    private String phoneNumber;
    private String confirmPassword;
}