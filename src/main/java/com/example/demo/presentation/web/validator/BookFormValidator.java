package com.example.demo.presentation.web.validator;

import com.example.demo.presentation.web.form.BookForm;
import com.example.demo.application.book.service.BookApplicationService;
import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.domain.book.valueobject.AgeRestriction;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
@AllArgsConstructor
public class BookFormValidator implements Validator {
    private static final long MAX_BOOK_FILE_SIZE = 20L * 1024 * 1024;
    private static final long MAX_COVER_FILE_SIZE = 10L * 1024 * 1024;

    @Autowired
    private final BookApplicationService bookService;

    @Override
    public boolean supports(Class<?> clazz) {
        return BookForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateForCreate(target, errors);
    }

    public void validateForCreate(Object target, Errors errors) {
        validateCommonFields(target, errors, true, true);

        if (errors.hasErrors()) {
            return;
        }

        validateDuplicate(-1, (BookForm) target, errors);
    }

    public void validateForUpdate(int bookId, Object target, Errors errors) {
        validateCommonFields(target, errors, false, false);

        if (errors.hasErrors()) {
            return;
        }

        validateDuplicate(bookId, (BookForm) target, errors);
    }

    private void validateCommonFields(Object target, Errors errors, boolean requireFiles, boolean requireAgeRestriction) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title.required", "Заполните название книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "author", "author.required", "Заполните автора книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "description.required", "Заполните описание книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "license", "license.required", "Заполните лицензию книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", "publisher.required", "Заполните издательство книги");

        BookForm bookDto = (BookForm) target;

        if (requireFiles && isEmpty(bookDto.getBookFile())) {
            errors.rejectValue("bookFile", "bookFile.required", "Выберите файл книги");
        } else if (!isEmpty(bookDto.getBookFile()) && bookDto.getBookFile().getSize() > MAX_BOOK_FILE_SIZE) {
            errors.rejectValue("bookFile", "bookFile.maxSize", "Файл книги должен быть не больше 20 МБ");
        }

        if (requireFiles && isEmpty(bookDto.getCoverFile())) {
            errors.rejectValue("coverFile", "coverFile.required", "Выберите файл обложки книги");
        } else if (!isEmpty(bookDto.getCoverFile()) && bookDto.getCoverFile().getSize() > MAX_COVER_FILE_SIZE) {
            errors.rejectValue("coverFile", "coverFile.maxSize", "Файл обложки должен быть не больше 10 МБ");
        }

        if (bookDto.getGenresList() == null || bookDto.getGenresList().isEmpty()) {
            errors.rejectValue("genresList", "genresList.required", "Выберите хотя бы один жанр книги");
        }

        if (requireAgeRestriction && (bookDto.getAgeRestriction() == null || bookDto.getAgeRestriction() == AgeRestriction.NOT_SELECTED)) {
            errors.rejectValue("ageRestriction", "ageRestriction.required", "Выберите возрастное ограничение");
        }

        if (bookDto.getPublishDate() == null) {
            errors.rejectValue("publishDate", "publishDate.required", "Заполните дату публикации книги");
        }

        if (bookDto.getPageCount() <= 0) {
            errors.rejectValue("pageCount", "pageCount.required", "Количество страниц должно быть строго больше нуля");
        }
    }

    private boolean isEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    private void validateDuplicate(int currentBookId, BookForm bookDto, Errors errors) {
        BookEntity existingBook = bookService.findBookByAuthorAndTitle(bookDto.getAuthor(), bookDto.getTitle());

        if (existingBook != null && existingBook.getId() != currentBookId) {
            errors.reject("bookAlreadyExists", "Книга такого автора с таким названием уже существует");
        }
    }
}
