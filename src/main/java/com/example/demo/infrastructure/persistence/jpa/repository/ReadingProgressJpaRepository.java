package com.example.demo.infrastructure.persistence.jpa.repository;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.infrastructure.persistence.jpa.entity.ReadingProgressEntity;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingProgressJpaRepository extends JpaRepository<ReadingProgressEntity, Integer> {
    ReadingProgressEntity findByBookAndUser(BookEntity book, UserEntity user);
    Page<ReadingProgressEntity> findAllByUser(UserEntity user, Pageable pageable);
}