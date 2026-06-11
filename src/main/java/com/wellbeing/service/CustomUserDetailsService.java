package com.wellbeing.service;

import java.util.Collections;

import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wellbeing.entity.Users;
import com.wellbeing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in DB"));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new CustomUserDetails(
                user.getId(), 
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(authority)
        );
    }
}