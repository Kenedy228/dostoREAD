package com.example.demo.domain.book.valueobject;

public record StorageObjectKey(String value) {
    public StorageObjectKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Storage object key must not be blank");
        }
    }
}
