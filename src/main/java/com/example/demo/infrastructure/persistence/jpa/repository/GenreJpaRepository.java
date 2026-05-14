package com.example.demo.infrastructure.persistence.jpa.repository;

import com.example.demo.infrastructure.persistence.jpa.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreJpaRepository extends JpaRepository<GenreEntity, Integer> {
    GenreEntity findGenreByName(String name);
}
