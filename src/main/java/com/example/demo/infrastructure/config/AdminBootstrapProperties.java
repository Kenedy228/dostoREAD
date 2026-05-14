package com.example.demo.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminBootstrapProperties(
        boolean enabled,
        String username,
        String email,
        String password
) {
}
