package com.example.demo.infrastructure.config;

import com.example.demo.infrastructure.persistence.jpa.entity.GenreEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.GenreJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
public class GenreBootstrapInitializer implements ApplicationRunner {
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

    private final GenreJpaRepository genreRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String genreName : DEFAULT_GENRES) {
            if (genreRepository.findGenreByName(genreName) == null) {
                genreRepository.save(new GenreEntity(genreName));
            }
        }
    }
}
