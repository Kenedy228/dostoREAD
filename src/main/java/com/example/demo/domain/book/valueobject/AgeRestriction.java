package com.example.demo.domain.book.valueobject;

import jakarta.persistence.Transient;
import lombok.Getter;

@Getter
public enum AgeRestriction {
    NOT_SELECTED("Не выбрано"),
    PG_0("0+"),
    PG_6("6+"),
    PG_12("12+"),
    PG_18("18+");

    private final String code;

    AgeRestriction(String code) {
        this.code = code;
    }
}
