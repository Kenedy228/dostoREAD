package com.example.demo.presentation.web.controller;

import com.example.demo.application.book.command.BookCommand;
import com.example.demo.presentation.web.form.BookForm;
import com.example.demo.application.book.service.BookApplicationService;
import com.example.demo.presentation.web.viewmodel.BookDetailsViewModel;
import com.example.demo.presentation.web.viewmodel.BookPageViewModel;
import com.example.demo.infrastructure.epub.EpubBookReader;
import com.example.demo.application.book.dto.ScreenFormat;
import com.example.demo.presentation.web.validator.BookFormValidator;
import com.example.demo.application.book.service.GenreApplicationService;
import com.example.demo.presentation.web.viewmodel.ReadingPositionViewModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final BookApplicationService bookService;
    @Autowired
    private final GenreApplicationService genreService;
    @Autowired
    private final BookFormValidator bookValidator;
    @Autowired
    private final EpubBookReader reader;

    @GetMapping("/admin-panel/books/create")
    public String form(Model model) {
        List<String> genresNames = genreService.getAllNames();

        model.addAttribute("bookDto", new BookForm());
        model.addAttribute("genresNames", genresNames);

        return "admin-panel/books/create";
    }

    @PostMapping("/admin-panel/books/create")
    public String create(@ModelAttribute BookForm bookDto, Model model, BindingResult bindingResult) throws Exception {
        bookValidator.validate(bookDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("bookDto", bookDto);
            model.addAttribute("genresNames", genreService.getAllNames());
            return "admin-panel/books/create";
        }

        bookService.create(toCommand(bookDto));

        return "redirect:/admin-panel";
    }

    @GetMapping("/admin-panel/books/view")
    public String viewBooks(Model model, @RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String searchQuery) throws Exception {
        BookPageViewModel pageData = bookService.getBooks(page, 3, null, searchQuery);

        model.addAttribute("pageData", pageData);
        model.addAttribute("searchQuery", searchQuery);

        return "admin-panel/books/view";
    }

    @GetMapping("/admin-panel/books/update")
    public String updateBook(@RequestParam int id, Model model) throws Exception {
        BookDetailsViewModel book = bookService.getBookDetails(id);
        List<String> genresNames = genreService.getAllNames();

        model.addAttribute("book", book);
        model.addAttribute("targetId", book.id());
        model.addAttribute("bookDto", new BookForm());
        model.addAttribute("genresNames", genresNames);

        return "admin-panel/books/update";
    }

    @PostMapping("/admin-panel/books/update")
    public String patchBook(@RequestParam int bookID, @ModelAttribute BookForm bookDto) throws Exception {

        bookService.update(bookID, toCommand(bookDto));

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
        BookPageViewModel pageData = bookService.getBooks(page, 3, pickedGenres, searchQuery);
        List<String> genresNames = genreService.getAllNames();

        model.addAttribute("pageData", pageData);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("genresNames", genresNames);
        model.addAttribute("pickedGenres", pickedGenres != null ? pickedGenres : List.of());

        return "user-panel/books/all-books";
    }

    @GetMapping("book-info")
    public String bookInfo(@RequestParam int id, Model model) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BookDetailsViewModel book = bookService.getBookDetails(id);
        ReadingPositionViewModel readingPosition = bookService.getReadingPosition(id, username);

        model.addAttribute("book", book);
        model.addAttribute("page", readingPosition.page());
        model.addAttribute("screen", readingPosition.screen());

        return "user-panel/books/book-info";
    }

    @GetMapping("read-book")
    public String readBook(@RequestParam int bookID, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "Arial") String font, @RequestParam(defaultValue = "0") int screen, Model model) throws Exception {
        InputStream bookFile = bookService.getBookFile(bookID);

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

        BookPageViewModel pageData = bookService.getBooksInReading(page, 3, username);

        model.addAttribute("pageData", pageData);

        return "user-panel/user-panel";
    }

    private BookCommand toCommand(BookForm form) {
        return new BookCommand(
                form.getTitle(),
                form.getAuthor(),
                form.getGenresList(),
                form.getBookFile(),
                form.getCoverFile(),
                form.getDescription(),
                form.getAgeRestriction(),
                form.getLicense(),
                form.getPublishDate(),
                form.getPublisher(),
                form.getPageCount()
        );
    }
}
