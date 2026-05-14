package com.example.demo.presentation.web.validator;

import com.example.demo.presentation.web.form.BookForm;
import com.example.demo.application.book.service.BookApplicationService;
import com.example.demo.domain.book.valueobject.AgeRestriction;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class BookFormValidator implements Validator {

    @Autowired
    private final BookApplicationService bookService;

    @Override
    public boolean supports(Class<?> clazz) {
        return BookForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title.required", "Заполните название книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "author", "author.required", "Заполните автора книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "description.required", "Заполните описание книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "license", "license.required", "Заполните лицензию книги");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", "publisher.required", "Заполните издательство книги");

        BookForm bookDto = (BookForm) target;

        if (bookDto.getBookFile().isEmpty()) {
            errors.rejectValue("bookFile", "bookFile.required", "Выберите файл книги");
        }

        if (bookDto.getCoverFile().isEmpty()) {
            errors.rejectValue("coverFile", "coverFile.required", "Выберите файл обложки книги");
        }

        if (bookDto.getGenresList() == null || bookDto.getGenresList().isEmpty()) {
            errors.rejectValue("genresList", "genresList.required", "Выберите хотя бы один жанр книги");
        }

        if (bookDto.getAgeRestriction() == null || bookDto.getAgeRestriction() == AgeRestriction.NOT_SELECTED) {
            errors.rejectValue("ageRestriction", "ageRestriction.required", "Выберите возрастное ограничение");
        }

        if (bookDto.getPublishDate() == null) {
            errors.rejectValue("publishDate", "publishDate.required", "Заполните дату публикации книги");
        }

        if (bookDto.getPageCount() <= 0) {
            errors.rejectValue("pageCount", "pageCount.required", "Количество страниц должно быть строго больше нуля");
        }

        if (errors.hasErrors()) {
            return;
        }

        if (bookService.findBookByAuthorAndTitle(bookDto.getAuthor(), bookDto.getTitle()) != null) {
            errors.reject("bookAlreadyExists", "Книга такого автора с таким названием уже существует");
        }
    }
}
