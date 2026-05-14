package com.example.demo.presentation.web.api;

import com.example.demo.presentation.web.api.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

@RestControllerAdvice(basePackages = "com.example.demo.presentation.web.api")
public class ApiExceptionHandler {
    private static final String FILE_LIMIT_MESSAGE = "Файл слишком большой. Обложка должна быть не больше 10 МБ, книга — не больше 20 МБ.";

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.fail(List.of(FILE_LIMIT_MESSAGE)));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMultipartException(MultipartException exception) {
        if (exception.getCause() instanceof MaxUploadSizeExceededException) {
            return handleMaxUploadSizeExceeded((MaxUploadSizeExceededException) exception.getCause());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(List.of(FILE_LIMIT_MESSAGE)));
    }
}
