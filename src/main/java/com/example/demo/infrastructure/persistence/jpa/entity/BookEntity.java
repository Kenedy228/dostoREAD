package com.example.demo.infrastructure.persistence.jpa.entity;

import com.example.demo.domain.book.valueobject.AgeRestriction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
public class BookEntity {
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
    @Column(nullable = false, length = 20)
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
    private List<GenreEntity> genres = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<ReadingProgressEntity> readingProgresses = new ArrayList<>();

    public void addGenre(GenreEntity genre) {
        genres.add(genre);
        genre.getBooks().add(this);
    }
}
