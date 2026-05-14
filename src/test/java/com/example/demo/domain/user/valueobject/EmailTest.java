package com.example.demo.domain.user.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {
    @Test
    void createsEmailWhenFormatIsValid() {
        Email email = new Email("reader@example.com");

        assertThat(email.value()).isEqualTo("reader@example.com");
    }

    @Test
    void rejectsInvalidEmail() {
        assertThatThrownBy(() -> new Email("reader"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid format");
    }
}
