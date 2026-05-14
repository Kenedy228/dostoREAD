package com.example.demo.domain.book.valueobject;

public record BookTitle(String value) {
    public BookTitle {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Book title must not be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Book title must be 100 characters or less");
        }
    }
}
