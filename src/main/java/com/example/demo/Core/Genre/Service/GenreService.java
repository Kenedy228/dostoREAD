package com.example.demo.Core.Genre.Service;

import com.example.demo.Core.Genre.Model.Genre;
import com.example.demo.Core.Genre.Repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {

    @Autowired
    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public List<String> getAllNames() {
        List<Genre> genres = findAll();

        List<String> genresNames = new ArrayList<>();

        for (Genre genre : genres) {
            genresNames.add(genre.getName());
        }

        return genresNames;
    }

    public Genre getByName(String name) {
        return genreRepository.findGenreByName(name);
    }

    public List<Genre> getGenresByNames(List<String> names) {
        List<Genre> genres = new ArrayList<>();

        if (names != null) {
            for (String name : names) {
                genres.add(getByName(name));
            }
        }

        return genres;
    }
}
