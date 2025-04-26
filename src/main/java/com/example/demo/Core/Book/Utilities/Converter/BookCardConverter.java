package com.example.demo.Core.Book.Utilities.Converter;

import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Core.Book.DTO.BookCard;
import com.example.demo.Core.Book.Utilities.Buckets.Buckets;
import com.example.demo.Core.Book.Utilities.MiniIO.MiniIO;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.lang.Iterable;

public class BookCardConverter {

    public BookCard convert(Book book) throws Exception {
        MiniIO miniIO = new MiniIO();

        return new BookCard(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                miniIO.getFileUrl(book.getPathToCover(), Buckets.Covers.getSource())
        );
    }

    public List<BookCard> convert(Iterable<Book> books) throws Exception {
        List<BookCard> cards = new ArrayList<>();

        for (Book book : books) {
            cards.add(convert(book));
        }

        return cards;
    }
}
