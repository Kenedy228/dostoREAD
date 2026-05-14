package com.example.demo.infrastructure.persistence.jpa.entity;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.infrastructure.persistence.jpa.entity.ReadingProgressEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    @Column(nullable = false, length = 60)
    private String password;
    @Column(nullable = true, unique = true)
    private String email;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    private boolean enabled = true;

    @OneToMany(mappedBy = "user")
    private List<ReadingProgressEntity> readingProgresses = new ArrayList<>();
}