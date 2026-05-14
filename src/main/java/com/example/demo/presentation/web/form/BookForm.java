package com.example.demo.presentation.web.form;

import com.example.demo.domain.book.valueobject.AgeRestriction;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class BookForm {
    private String title;
    private String author;
    private List<String> genresList;
    private MultipartFile bookFile;
    private MultipartFile coverFile;
    private String description;
    private AgeRestriction ageRestriction;
    private String license;
    private LocalDate publishDate;
    private String publisher;
    private short pageCount;
}