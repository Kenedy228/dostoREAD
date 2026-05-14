package com.example.demo.domain.book.model;

import com.example.demo.domain.book.valueobject.AuthorName;
import com.example.demo.domain.book.valueobject.BookId;
import com.example.demo.domain.book.valueobject.BookTitle;
import com.example.demo.domain.book.valueobject.StorageObjectKey;

public record Book(
        BookId id,
        BookTitle title,
        AuthorName author,
        StorageObjectKey bookFile,
        StorageObjectKey coverFile
) {
}
