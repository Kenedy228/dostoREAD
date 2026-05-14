package com.example.demo.application.book.service;

import com.example.demo.infrastructure.persistence.jpa.entity.GenreEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.GenreJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class GenreApplicationService {
    private static final List<String> DEFAULT_GENRES = List.of(
            "Фантастика",
            "Фэнтези",
            "Детектив",
            "Роман",
            "Приключения",
            "Драма",
            "Триллер",
            "Классика",
            "История",
            "Научная фантастика",
            "Боевик",
            "Ужасы"
    );

    @Autowired
    private final GenreJpaRepository genreRepository;

    public List<GenreEntity> findAll() {
        return genreRepository.findAll();
    }

    public List<String> getAllNames() {
        ensureDefaultGenres();
        List<GenreEntity> genres = findAll();

        List<String> genresNames = new ArrayList<>();

        for (GenreEntity genre : genres) {
            genresNames.add(genre.getName());
        }

        if (genresNames.isEmpty()) {
            return DEFAULT_GENRES;
        }

        return genresNames;
    }

    public GenreEntity getByName(String name) {
        return genreRepository.findGenreByName(name);
    }

    public List<GenreEntity> getGenresByNames(List<String> names) {
        List<GenreEntity> genres = new ArrayList<>();
        Set<String> uniqueNames = new LinkedHashSet<>();

        if (names != null) {
            for (String name : names) {
                if (name != null && !name.isBlank()) {
                    uniqueNames.add(name);
                }
            }
        }

        ensureDefaultGenres();

        for (String name : uniqueNames) {
            GenreEntity genre = getByName(name);

            if (genre == null) {
                genre = genreRepository.save(new GenreEntity(name));
            }

            genres.add(genre);
        }

        return genres;
    }

    private void ensureDefaultGenres() {
        for (String genreName : DEFAULT_GENRES) {
            if (getByName(genreName) == null) {
                genreRepository.save(new GenreEntity(genreName));
            }
        }
    }
}
