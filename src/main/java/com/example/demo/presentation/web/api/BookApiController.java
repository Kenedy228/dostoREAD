package com.example.demo.presentation.web.api;

import com.example.demo.application.book.command.BookCommand;
import com.example.demo.application.book.dto.ScreenFormat;
import com.example.demo.application.book.service.BookApplicationService;
import com.example.demo.application.book.service.GenreApplicationService;
import com.example.demo.infrastructure.epub.EpubBookReader;
import com.example.demo.presentation.web.api.dto.ApiResponse;
import com.example.demo.presentation.web.api.dto.BookReadPageResponse;
import com.example.demo.presentation.web.form.BookForm;
import com.example.demo.presentation.web.validator.BookFormValidator;
import com.example.demo.presentation.web.viewmodel.BookDetailsViewModel;
import com.example.demo.presentation.web.viewmodel.BookPageViewModel;
import com.example.demo.presentation.web.viewmodel.ReadingPositionViewModel;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BookApiController {
    private final BookApplicationService bookService;
    private final GenreApplicationService genreService;
    private final BookFormValidator bookValidator;
    private final EpubBookReader reader;

    @GetMapping("/genres")
    public ApiResponse<List<String>> genres() {
        return ApiResponse.ok(genreService.getAllNames());
    }

    @GetMapping("/books")
    public ApiResponse<BookPageViewModel> books(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(required = false) String searchQuery,
                                                @RequestParam(required = false) List<String> pickedGenres) throws Exception {
        return ApiResponse.ok(bookService.getBooks(page, 9, pickedGenres, searchQuery));
    }

    @GetMapping("/books/{id}")
    public ApiResponse<BookDetailsViewModel> book(@PathVariable int id) throws Exception {
        return ApiResponse.ok(bookService.getBookDetails(id));
    }

    @GetMapping("/books/{id}/reading-position")
    public ApiResponse<ReadingPositionViewModel> readingPosition(@PathVariable int id, Authentication authentication) {
        return ApiResponse.ok(bookService.getReadingPosition(id, authentication.getName()));
    }

    @GetMapping("/books/{id}/read")
    public ApiResponse<BookReadPageResponse> read(@PathVariable int id,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "0") int screen,
                                                  @RequestParam(defaultValue = "Inter") String font) throws Exception {
        ScreenFormat screenFormat = ScreenFormat.values()[Math.max(0, Math.min(screen, ScreenFormat.values().length - 1))];
        InputStream bookFile = bookService.getBookFile(id);
        List<List<String>> pages = reader.load(bookFile, screenFormat);
        int currentPage = Math.max(0, Math.min(page, pages.size() - 1));

        return ApiResponse.ok(new BookReadPageResponse(
                id,
                pages.get(currentPage),
                currentPage,
                pages.size(),
                screenFormat,
                font
        ));
    }

    @PostMapping("/books/{id}/progress")
    public ApiResponse<Void> saveProgress(@PathVariable int id,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "0") int screen,
                                          Authentication authentication) {
        bookService.setReadingProgress(id, authentication.getName(), page, screen);
        return ApiResponse.ok(null);
    }

    @GetMapping("/library/books")
    public ApiResponse<BookPageViewModel> library(@RequestParam(defaultValue = "0") int page,
                                                  Authentication authentication) throws Exception {
        return ApiResponse.ok(bookService.getBooksInReading(page, 9, authentication.getName()));
    }

    @GetMapping("/admin/books")
    public ApiResponse<BookPageViewModel> adminBooks(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(required = false) String searchQuery) throws Exception {
        return ApiResponse.ok(bookService.getBooks(page, 9, null, searchQuery));
    }

    @PostMapping("/admin/books")
    public ResponseEntity<ApiResponse<Void>> create(@ModelAttribute BookForm form, BindingResult bindingResult) throws Exception {
        bookValidator.validateForCreate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        bookService.create(toCommand(form));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PutMapping("/admin/books/{id}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable int id, @ModelAttribute BookForm form, BindingResult bindingResult) throws Exception {
        bookValidator.validateForUpdate(id, form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        bookService.update(id, toCommand(form));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/admin/books/{id}")
    public ApiResponse<Void> delete(@PathVariable int id) {
        bookService.delete(id);
        return ApiResponse.ok(null);
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
