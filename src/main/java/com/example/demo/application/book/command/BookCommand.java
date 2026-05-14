package com.example.demo.application.book.command;

import com.example.demo.domain.book.valueobject.AgeRestriction;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public record BookCommand(
        String title,
        String author,
        List<String> genresList,
        MultipartFile bookFile,
        MultipartFile coverFile,
        String description,
        AgeRestriction ageRestriction,
        String license,
        LocalDate publishDate,
        String publisher,
        short pageCount
) {
}
