package com.example.demo.presentation.web.api.dto;

public record RegisterRequest(String username, String email, String rawPassword, String rawPasswordConfirmation) {
}
