package com.example.demo.presentation.web.validator;

import com.example.demo.application.book.service.BookApplicationService;
import com.example.demo.domain.book.valueobject.AgeRestriction;
import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.presentation.web.form.BookForm;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookFormValidatorTest {

    @Test
    void rejectsBookAndCoverFilesThatAreTooLarge() {
        StubBookApplicationService bookService = new StubBookApplicationService();
        BookFormValidator validator = new BookFormValidator(bookService);

        BookForm form = createValidForm();
        form.setBookFile(new MockMultipartFile("bookFile", "book.epub", "application/epub+zip", new byte[20 * 1024 * 1024 + 1]));
        form.setCoverFile(new MockMultipartFile("coverFile", "cover.jpg", "image/jpeg", new byte[10 * 1024 * 1024 + 1]));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "book");
        validator.validate(form, bindingResult);

        assertThat(bindingResult.getFieldErrors("bookFile")).isNotEmpty();
        assertThat(bindingResult.getFieldErrors("coverFile")).isNotEmpty();
        assertThat(bindingResult.getFieldErrors("bookFile").get(0).getDefaultMessage()).isEqualTo("Файл книги должен быть не больше 20 МБ");
        assertThat(bindingResult.getFieldErrors("coverFile").get(0).getDefaultMessage()).isEqualTo("Файл обложки должен быть не больше 10 МБ");
    }

    @Test
    void acceptsFilesWithinLimits() {
        StubBookApplicationService bookService = new StubBookApplicationService();
        BookFormValidator validator = new BookFormValidator(bookService);

        BookForm form = createValidForm();
        form.setBookFile(new MockMultipartFile("bookFile", "book.epub", "application/epub+zip", new byte[20 * 1024 * 1024]));
        form.setCoverFile(new MockMultipartFile("coverFile", "cover.jpg", "image/jpeg", new byte[10 * 1024 * 1024]));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "book");
        validator.validate(form, bindingResult);

        assertThat(bindingResult.hasErrors()).isFalse();
    }

    @Test
    void allowsUpdateWithoutReuploadingFiles() {
        StubBookApplicationService bookService = new StubBookApplicationService();
        BookFormValidator validator = new BookFormValidator(bookService);

        BookForm form = createValidForm();
        form.setAgeRestriction(AgeRestriction.NOT_SELECTED);
        form.setBookFile(null);
        form.setCoverFile(null);

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "book");
        validator.validateForUpdate(7, form, bindingResult);

        assertThat(bindingResult.hasErrors()).isFalse();
    }

    @Test
    void rejectsUpdatingAnotherBookWithTheSameTitleAndAuthor() {
        StubBookApplicationService bookService = new StubBookApplicationService();
        BookFormValidator validator = new BookFormValidator(bookService);

        BookForm form = createValidForm();
        form.setBookFile(null);
        form.setCoverFile(null);

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "book");
        bookService.existingBookId = 11;
        validator.validateForUpdate(7, form, bindingResult);

        assertThat(bindingResult.hasErrors()).isTrue();
        assertThat(bindingResult.getGlobalErrors()).isNotEmpty();
    }

    private BookForm createValidForm() {
        BookForm form = new BookForm();
        form.setTitle("Title");
        form.setAuthor("Author");
        form.setGenresList(List.of("Fiction"));
        form.setDescription("Description");
        form.setAgeRestriction(AgeRestriction.PG_12);
        form.setLicense("License");
        form.setPublishDate(LocalDate.of(2025, 1, 1));
        form.setPublisher("Publisher");
        form.setPageCount((short) 100);
        return form;
    }

    private static final class StubBookApplicationService extends BookApplicationService {
        private int existingBookId = -1;

        private StubBookApplicationService() {
            super(null, null, null, null, null, null);
        }

        @Override
        public BookEntity findBookByAuthorAndTitle(String author, String title) {
            if (existingBookId < 0) {
                return null;
            }

            BookEntity book = new BookEntity();
            book.setId(existingBookId);
            book.setAuthor(author);
            book.setTitle(title);
            return book;
        }
    }
}
