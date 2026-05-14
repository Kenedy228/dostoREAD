package com.example.demo.application.library.service;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.infrastructure.persistence.jpa.entity.ReadingProgressEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.ReadingProgressJpaRepository;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReadingProgressApplicationService {

    @Autowired
    private final ReadingProgressJpaRepository readingProgressRepository;

    public ReadingProgressEntity save(ReadingProgressEntity readingProgress) {
        return readingProgressRepository.save(readingProgress);
    }

    public ReadingProgressEntity findByBookAndUser(BookEntity book, UserEntity user) {
        return readingProgressRepository.findByBookAndUser(book, user);
    }

    public Page<ReadingProgressEntity> findAllByUser(UserEntity user, Pageable pageable) {
        return readingProgressRepository.findAllByUser(user, pageable);
    }
}
