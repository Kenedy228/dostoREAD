package com.example.demo.Core.Genre.Repository;

import com.example.demo.Core.Genre.Model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Genre findGenreByName(String name);
}
