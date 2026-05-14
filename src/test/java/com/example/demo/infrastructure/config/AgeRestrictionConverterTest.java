package com.example.demo.infrastructure.config;

import com.example.demo.domain.book.valueobject.AgeRestriction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgeRestrictionConverterTest {

    private final AgeRestrictionConverter converter = new AgeRestrictionConverter();

    @Test
    void convertsCurrentEnumNames() {
        assertThat(converter.convert("PG_0")).isEqualTo(AgeRestriction.PG_0);
        assertThat(converter.convert("PG_6")).isEqualTo(AgeRestriction.PG_6);
        assertThat(converter.convert("PG_12")).isEqualTo(AgeRestriction.PG_12);
        assertThat(converter.convert("PG_18")).isEqualTo(AgeRestriction.PG_18);
    }

    @Test
    void convertsLegacyClientValues() {
        assertThat(converter.convert("ZERO_PLUS")).isEqualTo(AgeRestriction.PG_0);
        assertThat(converter.convert("SIX_PLUS")).isEqualTo(AgeRestriction.PG_6);
        assertThat(converter.convert("TWELVE_PLUS")).isEqualTo(AgeRestriction.PG_12);
        assertThat(converter.convert("EIGHTEEN_PLUS")).isEqualTo(AgeRestriction.PG_18);
    }
}
