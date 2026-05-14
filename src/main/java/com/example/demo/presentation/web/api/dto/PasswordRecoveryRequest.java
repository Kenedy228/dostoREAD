package com.example.demo.presentation.web.api.dto;

public record PasswordRecoveryRequest(String email, String rawPassword, String rawPasswordConfirmation) {
}
