package com.example.demo.infrastructure.persistence.jpa.repository;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<BookEntity, Integer> {
    List<BookEntity> findAll();

    BookEntity findBookById(int id);

    BookEntity findBookByAuthorAndTitle(String author, String title);

    Page<BookEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT b FROM BookEntity b JOIN b.genres g WHERE g.name in :genreNames group by b having count(distinct g) = :genreCount")
    Page<BookEntity> findAllByGenresNames(List<String> genreNames, long genreCount, Pageable pageable);

    @Query("SELECT DISTINCT b FROM BookEntity b JOIN b.genres g WHERE g.name in :genreNames AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<BookEntity> findAllByGenresNamesAndTitleContainingIgnoreCase(List<String> genreNames, String title, Pageable pageable);
}
