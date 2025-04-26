package com.example.demo.Core.Book.Utilities.ReadingProgress.Repository;

import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Core.Book.Utilities.ReadingProgress.Model.ReadingProgress;
import com.example.demo.Security.User.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Integer> {
    ReadingProgress findByBookAndUser(Book book, User user);
    Page<ReadingProgress> findAllByUser(User user, Pageable pageable);
}