package com.example.demo.domain.library.model;

import com.example.demo.domain.book.valueobject.BookId;
import com.example.demo.domain.library.valueobject.ReadingProgress;
import com.example.demo.domain.user.valueobject.UserId;

public record UserBook(UserId userId, BookId bookId, ReadingProgress progress) {
}
