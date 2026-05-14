package com.example.demo.infrastructure.security;

import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.infrastructure.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserApplicationService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUser(user);

        return userDetails;
    }
}
