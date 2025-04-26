package com.example.demo.Core.Book.Service;

import com.example.demo.Core.Book.DTO.BookDto;
import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Core.Book.Repository.BookRepository;
import com.example.demo.Core.Book.Utilities.BookBuilder.BookBuilder;
import com.example.demo.Core.Book.Utilities.BookBuilder.UpdateBookBuilder;
import com.example.demo.Core.Book.Utilities.Converter.BookCardConverter;
import com.example.demo.Core.Book.DTO.BookCard;
import com.example.demo.Core.Book.Utilities.Buckets.Buckets;
import com.example.demo.Core.Book.Utilities.MiniIO.MiniIO;
import com.example.demo.Core.Book.Pagination.BookPage;
import com.example.demo.Core.Book.Utilities.ReadingProgress.Model.ReadingProgress;
import com.example.demo.Core.Book.Utilities.ReadingProgress.Service.ReadingProgressService;
import com.example.demo.Core.Genre.Model.Genre;
import com.example.demo.Security.User.Model.User;
import com.example.demo.Security.User.Service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

@AllArgsConstructor
@Service
public class BookService {
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final MiniIO miniIO;
    @Autowired
    private final UserService userService;
    @Autowired
    private final ReadingProgressService readingProgressService;

    public Book findBookById(int id) {
        return bookRepository.findBookById(id);
    }

    @Transactional
    public Book create(BookDto bookDto, List<Genre> genres) throws Exception {

        String pathToBook = miniIO.uploadFile(Buckets.Book.getSource(), bookDto.getBookFile());
        String pathToCover = miniIO.uploadFile(Buckets.Covers.getSource(), bookDto.getCoverFile());

        BookBuilder builder = new BookBuilder();

        Book book = builder.build(
                bookDto.getTitle(),
                bookDto.getAuthor(),
                genres,
                pathToBook,
                pathToCover,
                bookDto.getDescription(),
                bookDto.getAgeRestriction(),
                bookDto.getLicense(),
                bookDto.getPublishDate(),
                bookDto.getPublisher(),
                bookDto.getPageCount()
        );

        return bookRepository.save(book);
    }

    public Book update(int bookID, BookDto bookDto, List<Genre> genres) throws Exception {
        Book book = bookRepository.findBookById(bookID);

        String pathToBook = "";
        String pathToCover = "";

        if (!bookDto.getBookFile().isEmpty()) {
            pathToBook = miniIO.uploadFile(Buckets.Book.getSource(), bookDto.getBookFile());
        }

        if (!bookDto.getCoverFile().isEmpty()) {
            pathToCover = miniIO.uploadFile(Buckets.Covers.getSource(), bookDto.getCoverFile());
        }

        UpdateBookBuilder builder = new UpdateBookBuilder();

        book = builder.build(
                bookDto.getTitle(),
                bookDto.getAuthor(),
                pathToBook,
                pathToCover,
                bookDto.getDescription(),
                bookDto.getAgeRestriction(),
                bookDto.getLicense(),
                bookDto.getPublishDate(),
                bookDto.getPublisher(),
                genres,
                bookDto.getPageCount(),
                book
        );

        return bookRepository.save(book);
    }

    public void delete(int id) {
        bookRepository.deleteById(id);
        bookRepository.flush();
    }

    public BookPage getBooks(int page, int size, List<String> pickedGenres, String searchQuery) throws Exception {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books;

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

        BookCardConverter converter = new BookCardConverter();
        List<BookCard> cards = converter.convert(books);

        return new BookPage(
                cards,
                books.getNumber(),
                books.getTotalPages(),
                buildBaseUrl(pickedGenres, searchQuery)
        );
    }

    public BookPage getBooksInReading(int page, int size, String username) throws Exception {
        User user = userService.findUserByUsername(username);
        Pageable pageable = PageRequest.of(page, size);

        Page<ReadingProgress> readingProgresses = readingProgressService.findAllByUser(user, pageable);

        List<Book> books = new ArrayList<>();

        for (ReadingProgress rp : readingProgresses) {
            books.add(rp.getBook());
        }

        BookCardConverter converter = new BookCardConverter();

        List<BookCard> cards = converter.convert(books);

        return new BookPage(
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

    public Book findBookByAuthorAndTitle(String author, String title) {
        return bookRepository.findBookByAuthorAndTitle(author, title);
    }

    @Transactional
    public Book setReadingProgress(int bookID, String username, int page, int screen) {
        Book book = bookRepository.findBookById(bookID);
        User user = userService.findUserByUsername(username);

        ReadingProgress readingProgress = readingProgressService.findByBookAndUser(book, user);

        if (readingProgress == null) {
            readingProgress = new ReadingProgress();
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
        Book saved = bookRepository.save(book);
        readingProgressService.save(readingProgress);

        return saved;
    }

    public ReadingProgress getReadingProgressOfUser(Book book, String username) {
        User user = userService.findUserByUsername(username);

        return readingProgressService.findByBookAndUser(book, user);
    }

    public InputStream getFile(String path, String bucketName) throws Exception {
        return miniIO.getFile(path, bucketName);
    }

    public String getFileUrl(String path, String bucketName) throws Exception {
        return miniIO.getFileUrl(path, bucketName);
    }
}
