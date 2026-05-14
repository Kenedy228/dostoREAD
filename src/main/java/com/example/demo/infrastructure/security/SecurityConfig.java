package com.example.demo.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> {
                    c.requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/library/**").hasRole("USER")
                            .requestMatchers("/api/books/*/reading-position").hasRole("USER")
                            .requestMatchers("/api/books/*/read").hasRole("USER")
                            .requestMatchers("/api/books/*/progress").hasRole("USER")
                            .requestMatchers("/api/books/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/api/genres").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/api/auth/session").permitAll()
                            .requestMatchers("/api/auth/logout").authenticated()
                            .requestMatchers("/api/auth/**").anonymous()
                            .anyRequest().permitAll();
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> {
                    logout.logoutUrl("/logout").logoutSuccessUrl("/");
                }).build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
