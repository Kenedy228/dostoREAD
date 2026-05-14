package com.example.demo.application.user.service;

import com.example.demo.presentation.web.viewmodel.AccountViewModel;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.UserJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserApplicationService {

    @Autowired
    private final UserJpaRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserEntity create(String username, String email, String rawPassword, String role) {
        UserEntity user = new UserEntity();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);

        return userRepository.save(user);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public UserEntity findUserByUsernameAndPassword(String username, String password) {
        return userRepository.findUserByUsernameAndPassword(username, password);
    }

    public UserEntity findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public boolean canLogin(String username, String rawPassword) {
        UserEntity user = userRepository.findUserByUsername(username);

        return user != null
                && passwordEncoder.matches(rawPassword, user.getPassword())
                && user.isEnabled();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findUserByUsername(username) != null;
    }

    public boolean isEnabled(String username) {
        UserEntity user = userRepository.findUserByUsername(username);

        return user != null && user.isEnabled();
    }

    public UserEntity findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public UserEntity updatePasswordByEmail(String email, String password) {
        UserEntity user = userRepository.findUserByEmail(email);

        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public UserEntity disableUser(int id) {
        UserEntity user = userRepository.findUserById(id);

        user.setEnabled(false);

        return userRepository.save(user);
    }

    public AccountViewModel disableAccount(int id) {
        return toAccountViewModel(disableUser(id));
    }

    public UserEntity enableUser(int id) {
        UserEntity user = userRepository.findUserById(id);

        user.setEnabled(true);

        return userRepository.save(user);
    }

    public AccountViewModel enableAccount(int id) {
        return toAccountViewModel(enableUser(id));
    }

    public List<UserEntity> findAllAdmins() {
        return userRepository.findAllByRole("ADMIN");
    }

    @Transactional(readOnly = true)
    public List<AccountViewModel> findAdminAccounts() {
        return findAllAdmins().stream()
                .map(this::toAccountViewModel)
                .toList();
    }

    public UserEntity findAdminByUsername(String username) {
        return userRepository.findUserByUsernameAndRole(username, "ADMIN");
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    private AccountViewModel toAccountViewModel(UserEntity user) {
        return new AccountViewModel(user.getId(), user.getUsername(), user.isEnabled());
    }
}
