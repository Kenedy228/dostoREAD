package com.example.demo.Core.Book.Repository;

import com.example.demo.Core.Book.Model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findAll();

    Book findBookById(int id);

    Book findBookByAuthorAndTitle(String author, String title);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.name in :genreNames group by b having count(distinct g) = :genreCount")
    Page<Book> findAllByGenresNames(List<String> genreNames, long genreCount, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.name in :genreNames AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Book> findAllByGenresNamesAndTitleContainingIgnoreCase(List<String> genreNames, String title, Pageable pageable);
}
