package com.example.demo.presentation.web.api.dto;

import java.util.List;

public record ApiResponse<T>(boolean ok, T data, List<String> errors) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, List.of());
    }

    public static <T> ApiResponse<T> fail(List<String> errors) {
        return new ApiResponse<>(false, null, errors);
    }
}
