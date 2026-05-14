package com.example.demo.presentation.web.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationForm {
    private String username;
    private String rawPassword;
    private String rawPasswordConfirmation;
    private String email;
}