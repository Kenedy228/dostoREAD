package com.example.demo.infrastructure.persistence.jpa.mapper;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.domain.book.valueobject.AgeRestriction;
import com.example.demo.infrastructure.persistence.jpa.entity.GenreEntity;

import java.time.LocalDate;
import java.util.List;

public class BookEntityBuilder {

    public BookEntity build(
            String title,
            String author,
            List<GenreEntity> genres,
            String pathToBook,
            String pathToCover,
            String description,
            AgeRestriction ageRestriction,
            String license,
            LocalDate publishDate,
            String publisher,
            short pagesCount
    ) {
        BookEntity book = new BookEntity();

        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setAgeRestriction(ageRestriction);
        book.setLicense(license);
        book.setPublishDate(publishDate);
        book.setPublisher(publisher);
        book.setPathToBook(pathToBook);
        book.setPageCount(pagesCount);
        book.setPathToCover(pathToCover);

        for (GenreEntity g : genres) {
            book.addGenre(g);
        }

        return book;
    }
}
