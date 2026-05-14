package com.example.demo.infrastructure.persistence.jpa.entity;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reading_progress")
@Data
@NoArgsConstructor
public class ReadingProgressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private int page;

    @Column(nullable = false)
    private int screen;
}