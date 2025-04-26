package com.example.demo.Core.Book.Controller;

import com.example.demo.Core.Book.DTO.BookDto;
import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Core.Book.Service.BookService;
import com.example.demo.Core.Book.Utilities.Buckets.Buckets;
import com.example.demo.Core.Book.Utilities.MiniIO.MiniIO;
import com.example.demo.Core.Book.Pagination.BookPage;
import com.example.demo.Core.Book.Utilities.Reader.Reader;
import com.example.demo.Core.Book.Utilities.Reader.ScreenFormat;
import com.example.demo.Core.Book.Utilities.ReadingProgress.Model.ReadingProgress;
import com.example.demo.Core.Book.Utilities.Validator.BookValidator;
import com.example.demo.Core.Genre.Model.Genre;
import com.example.demo.Core.Genre.Service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@Controller
@AllArgsConstructor
public class BookController {

    @Autowired
    private final BookService bookService;
    @Autowired
    private final GenreService genreService;
    @Autowired
    private final BookValidator bookValidator;
    @Autowired
    private final Reader reader;

    @GetMapping("/admin-panel/books/create")
    public String form(Model model) {
        List<String> genresNames = genreService.getAllNames();

        model.addAttribute("bookDto", new BookDto());
        model.addAttribute("genresNames", genresNames);

        return "admin-panel/books/create";
    }

    @PostMapping("/admin-panel/books/create")
    public String create(@ModelAttribute BookDto bookDto, Model model, BindingResult bindingResult) throws Exception {
        bookValidator.validate(bookDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("bookDto", bookDto);
            model.addAttribute("genresNames", genreService.getAllNames());
            return "admin-panel/books/create";
        }

        List<Genre> genres = genreService.getGenresByNames(bookDto.getGenresList());
        bookService.create(bookDto, genres);

        return "redirect:/admin-panel";
    }

    @GetMapping("/admin-panel/books/view")
    public String viewBooks(Model model, @RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String searchQuery) throws Exception {
        BookPage pageData = bookService.getBooks(page, 3, null, searchQuery);

        model.addAttribute("pageData", pageData);
        model.addAttribute("searchQuery", searchQuery);

        return "admin-panel/books/view";
    }

    @GetMapping("/admin-panel/books/update")
    public String updateBook(@RequestParam int id, Model model) throws Exception {
        Book book = bookService.findBookById(id);
        List<String> genresNames = genreService.getAllNames();

        String pathToBook = bookService.getFileUrl(book.getPathToBook(), Buckets.Book.getSource());
        String pathToCover = bookService.getFileUrl(book.getPathToCover(), Buckets.Covers.getSource());

        model.addAttribute("book", book);
        model.addAttribute("pathToBook", pathToBook);
        model.addAttribute("pathToCover", pathToCover);
        model.addAttribute("targetId", book.getId());
        model.addAttribute("bookDto", new BookDto());
        model.addAttribute("genresNames", genresNames);

        return "admin-panel/books/update";
    }

    @PostMapping("/admin-panel/books/update")
    public String patchBook(@RequestParam int bookID, @ModelAttribute BookDto bookDto) throws Exception {

        List<Genre> genres = genreService.getGenresByNames(bookDto.getGenresList());
        bookService.update(bookID, bookDto, genres);

        return "redirect:/admin-panel/books/view";
    }

    @GetMapping("admin-panel/books/delete")
    public String deleteBook(@RequestParam int id) {
        bookService.delete(id);

        return "redirect:/admin-panel/books/view";
    }

    @GetMapping("all-books")
    public String allBooks(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String searchQuery,
                           @RequestParam(required = false) List<String> pickedGenres) throws Exception {
        BookPage pageData = bookService.getBooks(page, 3, pickedGenres, searchQuery);
        List<String> genresNames = genreService.getAllNames();

        model.addAttribute("pageData", pageData);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("genresNames", genresNames);
        model.addAttribute("pickedGenres", pickedGenres != null ? pickedGenres : List.of());

        return "user-panel/books/all-books";
    }

    @GetMapping("book-info")
    public String bookInfo(@RequestParam int id, Model model) throws Exception {
        Book book = bookService.findBookById(id);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ReadingProgress readingProgress = bookService.getReadingProgressOfUser(book, username);

        int page = 0;
        int screen = 0;

        if (readingProgress != null) {
            page = readingProgress.getPage();
            screen = readingProgress.getScreen();
        }

        String coverUrl = bookService.getFileUrl(book.getPathToCover(), Buckets.Covers.getSource());

        model.addAttribute("book", book);
        model.addAttribute("cover", coverUrl);
        model.addAttribute("page", page);
        model.addAttribute("screen", screen);

        return "user-panel/books/book-info";
    }

    @GetMapping("read-book")
    public String readBook(@RequestParam int bookID, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "Arial") String font, @RequestParam(defaultValue = "0") int screen, Model model) throws Exception {
        Book book = bookService.findBookById(bookID);

        InputStream bookFile = bookService.getFile(book.getPathToBook(), Buckets.Book.getSource());

        List<List<String>> allPages = reader.load(bookFile, ScreenFormat.values()[screen]);

        page = Math.max(0, Math.min(page, allPages.size() - 1));

        model.addAttribute("bookID", bookID);
        model.addAttribute("bookPage", allPages.get(page));
        model.addAttribute("totalPages", allPages.size());
        model.addAttribute("currentPage", page);
        model.addAttribute("screen", ScreenFormat.values()[screen]);
        model.addAttribute("font", font);

        return "user-panel/books/read";
    }

    @GetMapping("/read-book/exit")
    public String saveProgress(@RequestParam int bookID, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int screen) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        bookService.setReadingProgress(bookID, username, page, screen);

        return "redirect:/";
    }

    @GetMapping("/user-panel")
    public String userPanel(@RequestParam(defaultValue = "0") int page, Model model) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BookPage pageData = bookService.getBooksInReading(page, 3, username);

        model.addAttribute("pageData", pageData);

        return "user-panel/user-panel";
    }
}
