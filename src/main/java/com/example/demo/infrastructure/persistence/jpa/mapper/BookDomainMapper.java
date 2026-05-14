package com.example.demo.infrastructure.persistence.jpa.mapper;

import com.example.demo.domain.book.model.Book;
import com.example.demo.domain.book.valueobject.AuthorName;
import com.example.demo.domain.book.valueobject.BookId;
import com.example.demo.domain.book.valueobject.BookTitle;
import com.example.demo.domain.book.valueobject.StorageObjectKey;
import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookDomainMapper {
    public Book toDomain(BookEntity entity) {
        return new Book(
                new BookId(entity.getId()),
                new BookTitle(entity.getTitle()),
                new AuthorName(entity.getAuthor()),
                new StorageObjectKey(entity.getPathToBook()),
                new StorageObjectKey(entity.getPathToCover())
        );
    }
}
