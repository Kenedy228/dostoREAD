package com.example.demo.Core.Book.Utilities.AgeRestrictions;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgeRestriction {
    NOT_SELECTED("Не выбрано"),
    PG_0("0+"),
    PG_6("6+"),
    PG_12("12+"),
    PG_18("18+");

    private final String code;
}
