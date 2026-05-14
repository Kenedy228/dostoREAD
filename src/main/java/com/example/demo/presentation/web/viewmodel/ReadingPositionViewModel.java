package com.example.demo.presentation.web.viewmodel;

public record ReadingPositionViewModel(int page, int screen) {
    public static ReadingPositionViewModel start() {
        return new ReadingPositionViewModel(0, 0);
    }
}
