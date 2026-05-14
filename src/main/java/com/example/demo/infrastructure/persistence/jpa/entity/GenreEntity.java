package com.example.demo.infrastructure.persistence.jpa.entity;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre")
@Getter
@Setter
@NoArgsConstructor
public class GenreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<BookEntity> books = new ArrayList<>();

    public GenreEntity(String name) {
        this.name = name;
    }
}
