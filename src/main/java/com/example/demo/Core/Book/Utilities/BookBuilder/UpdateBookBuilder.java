package com.example.demo.Core.Book.Utilities.BookBuilder;

import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Core.Book.Utilities.AgeRestrictions.AgeRestriction;
import com.example.demo.Core.Genre.Model.Genre;

import java.time.LocalDate;
import java.util.List;

public class UpdateBookBuilder {
    public Book build(
            String title,
            String author,
            String pathToBook,
            String pathToCover,
            String description,
            AgeRestriction ageRestriction,
            String license,
            LocalDate publishDate,
            String publisher,
            List<Genre> genres,
            short pagesCount,
            Book book
    ) {

        if (!title.isEmpty()) {
            book.setTitle(title);
        }

        if (!author.isEmpty()) {
            book.setAuthor(author);
        }

        if (!description.isEmpty()) {
            book.setDescription(description);
        }

        if (!ageRestriction.equals(AgeRestriction.NOT_SELECTED)) {
            book.setAgeRestriction(ageRestriction);
        }

        if (!license.isEmpty()) {
            book.setLicense(license);
        }

        if (!(publishDate == null)) {
            book.setPublishDate(publishDate);
        }

        if (!publisher.isEmpty()) {
            book.setPublisher(publisher);
        }

        if (!pathToBook.isEmpty()) {
            book.setPathToBook(pathToBook);
        }

        if (pagesCount > 0) {
            book.setPageCount(pagesCount);
        }

        if (!pathToCover.isEmpty()) {
            book.setPathToCover(pathToCover);
        }

        if (!genres.isEmpty()) {
            book.getGenres().clear();
            for (Genre g : genres) {
                book.addGenre(g);
            }
        }

        return book;
    }
}
