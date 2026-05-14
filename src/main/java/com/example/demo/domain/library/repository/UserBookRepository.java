package com.example.demo.domain.library.repository;

import com.example.demo.domain.book.valueobject.BookId;
import com.example.demo.domain.library.model.UserBook;
import com.example.demo.domain.user.valueobject.UserId;

import java.util.Optional;

public interface UserBookRepository {
    Optional<UserBook> findByUserIdAndBookId(UserId userId, BookId bookId);
    UserBook save(UserBook userBook);
}
