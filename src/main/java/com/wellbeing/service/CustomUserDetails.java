package com.wellbeing.service;

import com.wellbeing.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends User {

    private final String id;

    // 1. Patha constructor idhi (Okavela ekkadaina vadithe break avvakunda unchuthunnam)
    public CustomUserDetails(String id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        // Password null vasthe empty string pass chesthunnam safety kosam
        super(username, password != null ? password : "", authorities); 
        this.id = id;
    }

    // 2. KOTHA CONSTRUCTOR: Users entity ni direct ga theeskuntundi
    public CustomUserDetails(Users user) {
        super(
            user.getEmail(), // Username ga Email vaaduthunnam
            user.getPassword() != null ? user.getPassword() : "", // OAuth2 valla password null unna crash avvadu
            List.of(new SimpleGrantedAuthority(user.getRole())) // Role mapping
        );
        this.id = user.getId();
    }

    public String getId() {
        return id;
    }
}