package com.example.demo.infrastructure.config;

import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
    private static final String ADMIN_ROLE = "ADMIN";

    private final AdminBootstrapProperties properties;
    private final UserApplicationService userService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!properties.enabled()) {
            return;
        }

        if (isBlank(properties.username()) || isBlank(properties.password())) {
            throw new IllegalStateException("Admin bootstrap is enabled, but username or password is empty");
        }

        UserEntity user = userService.findUserByUsername(properties.username());
        if (user == null) {
            userService.create(properties.username(), emptyToNull(properties.email()), properties.password(), ADMIN_ROLE);
            return;
        }

        user.setRole(ADMIN_ROLE);
        user.setEnabled(true);

        if (!isBlank(properties.email())) {
            user.setEmail(properties.email());
        }

        user.setPassword(userService.encodePassword(properties.password()));
        userService.save(user);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String emptyToNull(String value) {
        return isBlank(value) ? null : value;
    }
}
