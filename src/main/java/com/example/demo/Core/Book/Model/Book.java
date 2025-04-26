package com.example.demo.Core.Book.Model;

import com.example.demo.Core.Book.Utilities.AgeRestrictions.AgeRestriction;
import com.example.demo.Core.Book.Utilities.ReadingProgress.Model.ReadingProgress;
import com.example.demo.Core.Genre.Model.Genre;
import com.example.demo.Security.User.Model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false)
    private String pathToBook;

    @Column(nullable = false)
    private String pathToCover;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "age_restriction_type",nullable = false)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    private AgeRestriction ageRestriction;

    @Column(nullable = false)
    private String license;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private short pageCount;

    @ManyToMany()
    @JoinTable(
            name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<ReadingProgress> readingProgresses = new ArrayList<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getBooks().add(this);
    }
}