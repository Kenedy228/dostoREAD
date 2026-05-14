package com.example.demo.domain.book.repository;

import com.example.demo.domain.book.model.Book;
import com.example.demo.domain.book.valueobject.BookId;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(BookId id);
    Book save(Book book);
}
