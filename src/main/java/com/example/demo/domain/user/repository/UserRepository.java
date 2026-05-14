package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.model.User;
import com.example.demo.domain.user.valueobject.Email;
import com.example.demo.domain.user.valueobject.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
}
