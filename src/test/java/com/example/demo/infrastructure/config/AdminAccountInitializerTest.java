package com.example.demo.infrastructure.config;

import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdminAccountInitializerTest {

    @Test
    void createsAdminWhenUserIsMissing() throws Exception {
        StubUserApplicationService userService = new StubUserApplicationService();
        AdminBootstrapProperties properties = new AdminBootstrapProperties(
                true,
                "admin",
                "admin@localhost",
                "Admin123!"
        );
        AdminAccountInitializer initializer = new AdminAccountInitializer(properties, userService);

        initializer.run(null);

        assertThat(userService.findUserByUsernameCalls).isEqualTo(1);
        assertThat(userService.createdUser).isNotNull();
        assertThat(userService.createdUser.getUsername()).isEqualTo("admin");
        assertThat(userService.createdUser.getEmail()).isEqualTo("admin@localhost");
        assertThat(userService.createdUser.getRole()).isEqualTo("ADMIN");
        assertThat(userService.createdUser.getPassword()).isEqualTo("encoded-Admin123!");
        assertThat(userService.savedUser).isNull();
    }

    @Test
    void synchronizesExistingUserWithAdminCredentials() throws Exception {
        StubUserApplicationService userService = new StubUserApplicationService();
        UserEntity existingUser = new UserEntity();
        existingUser.setId(1);
        existingUser.setUsername("admin");
        existingUser.setEmail("old@localhost");
        existingUser.setPassword("old-password");
        existingUser.setRole("USER");
        existingUser.setEnabled(false);
        userService.existingUser = existingUser;

        AdminBootstrapProperties properties = new AdminBootstrapProperties(
                true,
                "admin",
                "admin@localhost",
                "Admin123!"
        );
        AdminAccountInitializer initializer = new AdminAccountInitializer(properties, userService);

        initializer.run(null);

        assertThat(userService.findUserByUsernameCalls).isEqualTo(1);
        assertThat(userService.createdUser).isNull();
        assertThat(userService.savedUser).isSameAs(existingUser);
        assertThat(existingUser.getRole()).isEqualTo("ADMIN");
        assertThat(existingUser.isEnabled()).isTrue();
        assertThat(existingUser.getEmail()).isEqualTo("admin@localhost");
        assertThat(existingUser.getPassword()).isEqualTo("encoded-Admin123!");
    }

    private static final class StubUserApplicationService extends UserApplicationService {
        private UserEntity existingUser;
        private UserEntity createdUser;
        private UserEntity savedUser;
        private int findUserByUsernameCalls;

        private StubUserApplicationService() {
            super(null, null);
        }

        @Override
        public UserEntity findUserByUsername(String username) {
            findUserByUsernameCalls++;
            return existingUser;
        }

        @Override
        public UserEntity create(String username, String email, String rawPassword, String role) {
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(encodePassword(rawPassword));
            user.setRole(role);
            createdUser = user;
            return user;
        }

        @Override
        public String encodePassword(String rawPassword) {
            return "encoded-" + rawPassword;
        }

        @Override
        public UserEntity save(UserEntity user) {
            savedUser = user;
            return user;
        }
    }
}
