package com.example.demo.Core.Book.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookCard {
    private final int id;
    private final String title;
    private final String author;
    private final String coverUrl;
}