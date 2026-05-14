package com.example.demo.domain.book.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookTitleTest {
    @Test
    void createsTitleWhenValueIsValid() {
        BookTitle title = new BookTitle("Преступление и наказание");

        assertThat(title.value()).isEqualTo("Преступление и наказание");
    }

    @Test
    void rejectsBlankTitle() {
        assertThatThrownBy(() -> new BookTitle(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }
}
