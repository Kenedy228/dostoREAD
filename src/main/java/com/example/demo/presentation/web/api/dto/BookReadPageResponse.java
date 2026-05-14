package com.example.demo.presentation.web.api.dto;

import com.example.demo.application.book.dto.ScreenFormat;

import java.util.List;

public record BookReadPageResponse(
        int bookId,
        List<String> lines,
        int currentPage,
        int totalPages,
        ScreenFormat screen,
        String font
) {
}
