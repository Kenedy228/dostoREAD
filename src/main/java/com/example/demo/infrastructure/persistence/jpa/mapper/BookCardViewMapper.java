package com.example.demo.infrastructure.persistence.jpa.mapper;

import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.presentation.web.viewmodel.BookCardViewModel;
import com.example.demo.infrastructure.storage.minio.StorageBucket;
import com.example.demo.infrastructure.storage.minio.MinioStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.lang.Iterable;

@Component
@AllArgsConstructor
public class BookCardViewMapper {
    private final MinioStorageService miniIO;

    public BookCardViewModel convert(BookEntity book) throws Exception {
        return new BookCardViewModel(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                miniIO.getFileUrl(book.getPathToCover(), StorageBucket.COVERS.getSource())
        );
    }

    public List<BookCardViewModel> convert(Iterable<BookEntity> books) throws Exception {
        List<BookCardViewModel> cards = new ArrayList<>();

        for (BookEntity book : books) {
            cards.add(convert(book));
        }

        return cards;
    }
}
