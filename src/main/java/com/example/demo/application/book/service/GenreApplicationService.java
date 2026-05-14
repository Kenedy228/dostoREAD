package com.example.demo.application.book.service;

import com.example.demo.infrastructure.persistence.jpa.entity.GenreEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.GenreJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GenreApplicationService {

    @Autowired
    private final GenreJpaRepository genreRepository;

    public List<GenreEntity> findAll() {
        return genreRepository.findAll();
    }

    public List<String> getAllNames() {
        List<GenreEntity> genres = findAll();

        List<String> genresNames = new ArrayList<>();

        for (GenreEntity genre : genres) {
            genresNames.add(genre.getName());
        }

        return genresNames;
    }

    public GenreEntity getByName(String name) {
        return genreRepository.findGenreByName(name);
    }

    public List<GenreEntity> getGenresByNames(List<String> names) {
        List<GenreEntity> genres = new ArrayList<>();

        if (names != null) {
            for (String name : names) {
                genres.add(getByName(name));
            }
        }

        return genres;
    }
}
