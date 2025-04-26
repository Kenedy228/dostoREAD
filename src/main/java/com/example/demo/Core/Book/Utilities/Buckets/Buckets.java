package com.example.demo.Core.Book.Utilities.Buckets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Buckets {
    Book("books"),
    Covers("covers");

    private final String source;
}
