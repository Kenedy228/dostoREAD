package com.example.demo.infrastructure.config;

import com.example.demo.domain.book.valueobject.AgeRestriction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class AgeRestrictionConverter implements Converter<String, AgeRestriction> {
    private static final Map<String, AgeRestriction> ALIASES = Map.of(
            "ZERO_PLUS", AgeRestriction.PG_0,
            "SIX_PLUS", AgeRestriction.PG_6,
            "TWELVE_PLUS", AgeRestriction.PG_12,
            "EIGHTEEN_PLUS", AgeRestriction.PG_18
    );

    @Override
    public AgeRestriction convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        String normalized = source.trim().toUpperCase(Locale.ROOT);

        AgeRestriction alias = ALIASES.get(normalized);
        if (alias != null) {
            return alias;
        }

        return AgeRestriction.valueOf(normalized);
    }
}
