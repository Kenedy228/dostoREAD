package com.example.demo.application.book.service;

import com.example.demo.application.book.command.BookCommand;
import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.BookJpaRepository;
import com.example.demo.infrastructure.persistence.jpa.mapper.BookEntityBuilder;
import com.example.demo.infrastructure.persistence.jpa.mapper.UpdateBookEntityBuilder;
import com.example.demo.infrastructure.persistence.jpa.mapper.BookCardViewMapper;
import com.example.demo.presentation.web.viewmodel.BookCardViewModel;
import com.example.demo.infrastructure.storage.minio.StorageBucket;
import com.example.demo.infrastructure.storage.minio.MinioStorageService;
import com.example.demo.presentation.web.viewmodel.BookDetailsViewModel;
import com.example.demo.presentation.web.viewmodel.BookPageViewModel;
import com.example.demo.presentation.web.viewmodel.ReadingPositionViewModel;
import com.example.demo.infrastructure.persistence.jpa.entity.ReadingProgressEntity;
import com.example.demo.application.library.service.ReadingProgressApplicationService;
import com.example.demo.infrastructure.persistence.jpa.entity.GenreEntity;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import com.example.demo.application.user.service.UserApplicationService;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

@AllArgsConstructor
@Service
public class BookApplicationService {
    @Autowired
    private final BookJpaRepository bookRepository;
    @Autowired
    private final MinioStorageService miniIO;
    @Autowired
    private final UserApplicationService userService;
    @Autowired
    private final ReadingProgressApplicationService readingProgressService;
    @Autowired
    private final BookCardViewMapper bookCardViewMapper;
    @Autowired
    private final GenreApplicationService genreService;

    public BookEntity findBookById(int id) {
        return bookRepository.findBookById(id);
    }

    @Transactional(readOnly = true)
    public BookDetailsViewModel getBookDetails(int id) throws Exception {
        BookEntity book = bookRepository.findBookById(id);

        return toDetailsViewModel(book);
    }

    @Transactional
    public BookEntity create(BookCommand bookDto) throws Exception {
        return create(bookDto, genreService.getGenresByNames(bookDto.genresList()));
    }

    @Transactional
    public BookEntity create(BookCommand bookDto, List<GenreEntity> genres) throws Exception {

        String pathToBook = miniIO.uploadFile(StorageBucket.BOOK.getSource(), bookDto.bookFile());
        String pathToCover = miniIO.uploadFile(StorageBucket.COVERS.getSource(), bookDto.coverFile());

        BookEntityBuilder builder = new BookEntityBuilder();

        BookEntity book = builder.build(
                bookDto.title(),
                bookDto.author(),
                genres,
                pathToBook,
                pathToCover,
                bookDto.description(),
                bookDto.ageRestriction(),
                bookDto.license(),
                bookDto.publishDate(),
                bookDto.publisher(),
                bookDto.pageCount()
        );

        return bookRepository.save(book);
    }

    public BookEntity update(int bookID, BookCommand bookDto) throws Exception {
        return update(bookID, bookDto, genreService.getGenresByNames(bookDto.genresList()));
    }

    public BookEntity update(int bookID, BookCommand bookDto, List<GenreEntity> genres) throws Exception {
        BookEntity book = bookRepository.findBookById(bookID);

        String pathToBook = "";
        String pathToCover = "";

        if (!bookDto.bookFile().isEmpty()) {
            pathToBook = miniIO.uploadFile(StorageBucket.BOOK.getSource(), bookDto.bookFile());
        }

        if (!bookDto.coverFile().isEmpty()) {
            pathToCover = miniIO.uploadFile(StorageBucket.COVERS.getSource(), bookDto.coverFile());
        }

        UpdateBookEntityBuilder builder = new UpdateBookEntityBuilder();

        book = builder.build(
                bookDto.title(),
                bookDto.author(),
                pathToBook,
                pathToCover,
                bookDto.description(),
                bookDto.ageRestriction(),
                bookDto.license(),
                bookDto.publishDate(),
                bookDto.publisher(),
                genres,
                bookDto.pageCount(),
                book
        );

        return bookRepository.save(book);
    }

    public void delete(int id) {
        bookRepository.deleteById(id);
        bookRepository.flush();
    }

    @Transactional(readOnly = true)
    public BookPageViewModel getBooks(int page, int size, List<String> pickedGenres, String searchQuery) throws Exception {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> books;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            if (pickedGenres != null && !pickedGenres.isEmpty()) {
                books = bookRepository.findAllByGenresNamesAndTitleContainingIgnoreCase(pickedGenres, searchQuery, pageable);
            } else {
                books = bookRepository.findByTitleContainingIgnoreCase(searchQuery, pageable);
            }
        } else if (pickedGenres != null && !pickedGenres.isEmpty()) {
            books = bookRepository.findAllByGenresNames(pickedGenres, pickedGenres.size(), pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }

        List<BookCardViewModel> cards = bookCardViewMapper.convert(books);

        return new BookPageViewModel(
                cards,
                books.getNumber(),
                books.getTotalPages(),
                buildBaseUrl(pickedGenres, searchQuery)
        );
    }

    @Transactional(readOnly = true)
    public BookPageViewModel getBooksInReading(int page, int size, String username) throws Exception {
        UserEntity user = userService.findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);

        Page<ReadingProgressEntity> readingProgresses = readingProgressService.findAllByUser(user, pageable);

        List<BookEntity> books = new ArrayList<>();

        for (ReadingProgressEntity rp : readingProgresses) {
            books.add(rp.getBook());
        }

        List<BookCardViewModel> cards = bookCardViewMapper.convert(books);

        return new BookPageViewModel(
                cards,
                readingProgresses.getNumber(),
                readingProgresses.getTotalPages(),
                buildBaseUrl(null, null)
        );
    }

    private String buildBaseUrl(List<String> pickedGenres, String searchQuery) {
        StringBuilder url = new StringBuilder("/all-books?");

        if (pickedGenres != null && !pickedGenres.isEmpty()) {
            StringBuilder genres = new StringBuilder();

            for (String g : pickedGenres) {
                genres.append("pickedGenres=").append(g);
            }

            url.append("pickedGenres=").append(URLEncoder.encode(genres.toString(), StandardCharsets.UTF_8)).append("&");
        }

        if (searchQuery != null && !searchQuery.isEmpty()) {
            url.append("query=").append(URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)).append("&");
        }
        return url.toString();
    }

    public BookEntity findBookByAuthorAndTitle(String author, String title) {
        return bookRepository.findBookByAuthorAndTitle(author, title);
    }

    @Transactional
    public BookEntity setReadingProgress(int bookID, String username, int page, int screen) {
        BookEntity book = bookRepository.findBookById(bookID);
        UserEntity user = userService.findUserByUsername(username);

        ReadingProgressEntity readingProgress = readingProgressService.findByBookAndUser(book, user);

        if (readingProgress == null) {
            readingProgress = new ReadingProgressEntity();
            readingProgress.setBook(book);
            readingProgress.setUser(user);
            readingProgress.setPage(page);
            readingProgress.setScreen(screen);
        } else {
            readingProgress.setPage(page);
            readingProgress.setScreen(screen);
        }

        book.getReadingProgresses().add(readingProgress);
        user.getReadingProgresses().add(readingProgress);

        userService.save(user);
        BookEntity saved = bookRepository.save(book);
        readingProgressService.save(readingProgress);

        return saved;
    }

    public ReadingProgressEntity getReadingProgressOfUser(BookEntity book, String username) {
        UserEntity user = userService.findUserByUsername(username);

        return readingProgressService.findByBookAndUser(book, user);
    }

    @Transactional(readOnly = true)
    public ReadingPositionViewModel getReadingPosition(int bookId, String username) {
        BookEntity book = bookRepository.findBookById(bookId);
        UserEntity user = userService.findUserByUsername(username);
        ReadingProgressEntity readingProgress = readingProgressService.findByBookAndUser(book, user);

        if (readingProgress == null) {
            return ReadingPositionViewModel.start();
        }

        return new ReadingPositionViewModel(readingProgress.getPage(), readingProgress.getScreen());
    }

    @Transactional(readOnly = true)
    public InputStream getBookFile(int bookId) throws Exception {
        BookEntity book = bookRepository.findBookById(bookId);

        return miniIO.getFile(book.getPathToBook(), StorageBucket.BOOK.getSource());
    }

    public InputStream getFile(String path, String bucketName) throws Exception {
        return miniIO.getFile(path, bucketName);
    }

    public String getFileUrl(String path, String bucketName) throws Exception {
        return miniIO.getFileUrl(path, bucketName);
    }

    private BookDetailsViewModel toDetailsViewModel(BookEntity book) throws Exception {
        return new BookDetailsViewModel(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getGenres().stream().map(GenreEntity::getName).toList(),
                book.getAgeRestriction().getCode(),
                book.getLicense(),
                book.getPublisher(),
                book.getPublishDate(),
                book.getPageCount(),
                miniIO.getFileUrl(book.getPathToCover(), StorageBucket.COVERS.getSource()),
                miniIO.getFileUrl(book.getPathToBook(), StorageBucket.BOOK.getSource())
        );
    }
}
