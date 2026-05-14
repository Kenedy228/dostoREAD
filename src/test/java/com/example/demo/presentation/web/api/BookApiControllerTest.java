package com.example.demo.presentation.web.api;

import com.example.demo.application.book.command.BookCommand;
import com.example.demo.application.book.service.BookApplicationService;
import com.example.demo.domain.book.valueobject.AgeRestriction;
import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.presentation.web.form.BookForm;
import com.example.demo.presentation.web.validator.BookFormValidator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookApiControllerTest {

    @Test
    void createsBookThroughControllerWhenFormIsValid() throws Exception {
        StubBookApplicationService bookService = new StubBookApplicationService();
        BookApiController controller = new BookApiController(
                bookService,
                null,
                new BookFormValidator(bookService),
                null
        );

        BookForm form = createCreateForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "book");

        var response = controller.create(form, bindingResult);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().ok()).isTrue();
        assertThat(bookService.createCalled).isTrue();
        assertThat(bookService.lastCommand.title()).isEqualTo("Title");
        assertThat(bookService.lastCommand.genresList()).containsExactly("Фантастика");
    }

    @Test
    void updatesBookWithoutReuploadingFilesThroughController() throws Exception {
        StubBookApplicationService bookService = new StubBookApplicationService();
        BookApiController controller = new BookApiController(
                bookService,
                null,
                new BookFormValidator(bookService),
                null
        );

        BookForm form = createUpdateForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "book");

        var response = controller.update(7, form, bindingResult);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().ok()).isTrue();
        assertThat(bookService.updateCalled).isTrue();
        assertThat(bookService.lastUpdateId).isEqualTo(7);
        assertThat(bookService.lastCommand.ageRestriction()).isEqualTo(AgeRestriction.NOT_SELECTED);
        assertThat(bookService.lastCommand.bookFile()).isNull();
        assertThat(bookService.lastCommand.coverFile()).isNull();
    }

    private BookForm createCreateForm() {
        BookForm form = new BookForm();
        form.setTitle("Title");
        form.setAuthor("Author");
        form.setGenresList(List.of("Фантастика"));
        form.setDescription("Description");
        form.setAgeRestriction(AgeRestriction.PG_12);
        form.setLicense("License");
        form.setPublishDate(LocalDate.of(2025, 1, 1));
        form.setPublisher("Publisher");
        form.setPageCount((short) 123);
        form.setBookFile(new MockMultipartFile("bookFile", "book.epub", "application/epub+zip", new byte[1024]));
        form.setCoverFile(new MockMultipartFile("coverFile", "cover.jpg", "image/jpeg", new byte[1024]));
        return form;
    }

    private BookForm createUpdateForm() {
        BookForm form = createCreateForm();
        form.setAgeRestriction(AgeRestriction.NOT_SELECTED);
        form.setBookFile(null);
        form.setCoverFile(null);
        return form;
    }

    private static final class StubBookApplicationService extends BookApplicationService {
        private boolean createCalled;
        private boolean updateCalled;
        private int lastUpdateId;
        private BookCommand lastCommand;

        private StubBookApplicationService() {
            super(null, null, null, null, null, null);
        }

        @Override
        public BookEntity create(BookCommand bookDto) {
            createCalled = true;
            lastCommand = bookDto;
            return null;
        }

        @Override
        public BookEntity update(int bookID, BookCommand bookDto) {
            updateCalled = true;
            lastUpdateId = bookID;
            lastCommand = bookDto;
            return null;
        }

        @Override
        public BookEntity findBookByAuthorAndTitle(String author, String title) {
            return null;
        }
    }
}
