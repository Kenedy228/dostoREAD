package com.example.demo.Core.Book.Pagination;

import com.example.demo.Core.Book.DTO.BookCard;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookPage {
    private List<BookCard> cards;
    private int currentPage;
    private int totalPages;
    private String url;

    public boolean hasElements() {
        return totalPages > 0;
    }
}
