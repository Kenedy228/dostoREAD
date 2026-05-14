package com.example.demo.domain.book.valueobject;

public record AuthorName(String value) {
    public AuthorName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Author name must not be blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Author name must be 100 characters or less");
        }
    }
}
