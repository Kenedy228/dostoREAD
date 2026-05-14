package com.example.demo.domain.library.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReadingProgressTest {
    @Test
    void createsProgressForNonNegativeValues() {
        ReadingProgress progress = new ReadingProgress(10, 1);

        assertThat(progress.page()).isEqualTo(10);
        assertThat(progress.screen()).isEqualTo(1);
    }

    @Test
    void rejectsNegativePage() {
        assertThatThrownBy(() -> new ReadingProgress(-1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be negative");
    }
}
