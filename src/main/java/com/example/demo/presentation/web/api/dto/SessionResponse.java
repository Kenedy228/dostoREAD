package com.example.demo.presentation.web.api.dto;

public record SessionResponse(boolean authenticated, String username, String role) {
}
