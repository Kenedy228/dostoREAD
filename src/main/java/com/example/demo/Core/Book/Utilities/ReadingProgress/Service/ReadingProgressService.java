package com.example.demo.Core.Book.Utilities.ReadingProgress.Service;

import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Core.Book.Utilities.ReadingProgress.Model.ReadingProgress;
import com.example.demo.Core.Book.Utilities.ReadingProgress.Repository.ReadingProgressRepository;
import com.example.demo.Security.User.Model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReadingProgressService {

    @Autowired
    private final ReadingProgressRepository readingProgressRepository;

    public ReadingProgress save(ReadingProgress readingProgress) {
        return readingProgressRepository.save(readingProgress);
    }

    public ReadingProgress findByBookAndUser(Book book, User user) {
        return readingProgressRepository.findByBookAndUser(book, user);
    }

    public Page<ReadingProgress> findAllByUser(User user, Pageable pageable) {
        return readingProgressRepository.findAllByUser(user, pageable);
    }
}
