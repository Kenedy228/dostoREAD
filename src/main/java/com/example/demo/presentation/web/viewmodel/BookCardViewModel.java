package com.example.demo.presentation.web.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookCardViewModel {
    private final int id;
    private final String title;
    private final String author;
    private final String coverUrl;
}
