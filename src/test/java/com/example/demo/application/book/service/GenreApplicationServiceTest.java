package com.example.demo.application.book.service;

import com.example.demo.infrastructure.persistence.jpa.entity.GenreEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.GenreJpaRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GenreApplicationServiceTest {

    @Test
    void returnsDefaultGenresWhenRepositoryIsEmpty() {
        InMemoryGenreRepository repository = new InMemoryGenreRepository();
        GenreApplicationService service = new GenreApplicationService(repository.proxy());

        assertThat(service.getAllNames())
                .contains("Фантастика", "Фэнтези", "Детектив");
    }

    @Test
    void createsMissingGenresOnDemandWhenBooksAreSaved() {
        InMemoryGenreRepository repository = new InMemoryGenreRepository();
        GenreApplicationService service = new GenreApplicationService(repository.proxy());

        List<GenreEntity> genres = service.getGenresByNames(List.of("Фантастика", "Киберпанк"));

        assertThat(genres).extracting(GenreEntity::getName).containsExactly("Фантастика", "Киберпанк");
        assertThat(repository.findAll()).extracting(GenreEntity::getName).contains("Киберпанк");
    }

    private static final class InMemoryGenreRepository implements InvocationHandler {
        private final Map<String, GenreEntity> store = new LinkedHashMap<>();
        private final GenreJpaRepository proxy = (GenreJpaRepository) Proxy.newProxyInstance(
                GenreJpaRepository.class.getClassLoader(),
                new Class<?>[]{GenreJpaRepository.class},
                this
        );

        GenreJpaRepository proxy() {
            return proxy;
        }

        List<GenreEntity> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findGenreByName" -> store.get(args[0]);
                case "save" -> {
                    GenreEntity genre = (GenreEntity) args[0];
                    if (genre.getId() == null) {
                        genre.setId(store.size() + 1);
                    }
                    store.put(genre.getName(), genre);
                    yield genre;
                }
                case "findAll" -> new ArrayList<>(store.values());
                default -> throw new UnsupportedOperationException("Method not implemented in test stub: " + method.getName());
            };
        }
    }
}
