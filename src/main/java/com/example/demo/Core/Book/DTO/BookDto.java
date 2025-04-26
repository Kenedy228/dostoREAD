package com.example.demo.Core.Book.DTO;

import com.example.demo.Core.Book.Utilities.AgeRestrictions.AgeRestriction;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class BookDto {
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