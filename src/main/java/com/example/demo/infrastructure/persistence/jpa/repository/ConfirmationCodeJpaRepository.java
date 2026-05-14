package com.example.demo.infrastructure.persistence.jpa.repository;

import com.example.demo.infrastructure.persistence.jpa.entity.ConfirmationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmationCodeJpaRepository extends JpaRepository<ConfirmationCodeEntity, Integer> {
    ConfirmationCodeEntity findConfirmationCodeByEmail(String email);
    List<ConfirmationCodeEntity> deleteConfirmationCodesByEmail(String email);
}
