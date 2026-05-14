package com.example.demo.presentation.web.api;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

final class ApiValidation {
    private ApiValidation() {
    }

    static List<String> errors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();
    }
}
