package com.example.demo.domain.user.model;

import com.example.demo.domain.user.valueobject.Email;
import com.example.demo.domain.user.valueobject.UserId;

public record User(
        UserId id,
        String username,
        Email email,
        String role,
        boolean enabled
) {
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
    }
}
