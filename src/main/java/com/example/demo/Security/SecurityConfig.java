package com.example.demo.Security;

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
                    c.requestMatchers("/admin-panel").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/books/view").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/books/create").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/books/delete").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/books/update").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/accounts/create").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/accounts/view").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/accounts/disable").hasRole("ADMIN")
                            .requestMatchers("/admin-panel/accounts/enable").hasRole("ADMIN")
                            .requestMatchers("/all-books").hasRole("USER")
                            .requestMatchers("/book-info").hasRole("USER")
                            .requestMatchers("/read-book").hasRole("USER")
                            .requestMatchers("/user-panel").hasRole("USER")
                            .requestMatchers("/login").anonymous()
                            .requestMatchers("/registration").anonymous()
                            .requestMatchers("/registration/email-confirmation").anonymous()
                            .requestMatchers("/password-recovery").anonymous()
                            .requestMatchers("/password-recovery/email-confirmation").anonymous()
                            .requestMatchers("/password-recovery/email-confirmation/change-password").anonymous()
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
