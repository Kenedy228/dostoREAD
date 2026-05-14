package com.example.demo.domain.user.valueobject;

public record UserId(int value) {
    public UserId {
        if (value <= 0) {
            throw new IllegalArgumentException("User id must be positive");
        }
    }
}
