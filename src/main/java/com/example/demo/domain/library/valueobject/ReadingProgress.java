package com.example.demo.domain.library.valueobject;

public record ReadingProgress(int page, int screen) {
    public ReadingProgress {
        if (page < 0) {
            throw new IllegalArgumentException("Reading page must not be negative");
        }
        if (screen < 0) {
            throw new IllegalArgumentException("Screen format index must not be negative");
        }
    }
}
