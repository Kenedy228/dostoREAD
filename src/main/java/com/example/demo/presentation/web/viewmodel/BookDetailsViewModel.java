package com.example.demo.presentation.web.viewmodel;

import java.time.LocalDate;
import java.util.List;

public record BookDetailsViewModel(
        int id,
        String title,
        String author,
        String description,
        List<String> genreNames,
        String ageRestrictionCode,
        String license,
        String publisher,
        LocalDate publishDate,
        short pageCount,
        String coverUrl,
        String bookUrl
) {
}
