package com.example.demo.infrastructure.persistence.jpa.repository;

import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findUserByUsernameAndPassword(String username, String password);
    UserEntity findUserByEmail(String email);
    UserEntity findUserByUsername(String username);
    List<UserEntity> findAllByRole(String role);
    UserEntity findUserById(int id);
    UserEntity findUserByUsernameAndRole(String username, String role);
}
