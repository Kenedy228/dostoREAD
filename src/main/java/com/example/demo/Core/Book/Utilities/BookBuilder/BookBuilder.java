package com.example.demo.Core.Book.Utilities.BookBuilder;

import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Core.Book.Utilities.AgeRestrictions.AgeRestriction;
import com.example.demo.Core.Genre.Model.Genre;

import java.time.LocalDate;
import java.util.List;

public class BookBuilder {

    public Book build(
            String title,
            String author,
            List<Genre> genres,
            String pathToBook,
            String pathToCover,
            String description,
            AgeRestriction ageRestriction,
            String license,
            LocalDate publishDate,
            String publisher,
            short pagesCount
    ) {
        Book book = new Book();

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

        for (Genre g : genres) {
            book.addGenre(g);
        }

        return book;
    }
}
