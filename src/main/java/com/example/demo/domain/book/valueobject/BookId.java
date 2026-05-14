package com.example.demo.domain.book.valueobject;

public record BookId(int value) {
    public BookId {
        if (value <= 0) {
            throw new IllegalArgumentException("Book id must be positive");
        }
    }
}
