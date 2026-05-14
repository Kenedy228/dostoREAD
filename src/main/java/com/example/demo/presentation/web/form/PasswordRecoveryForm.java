package com.example.demo.presentation.web.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordRecoveryForm {
    private String email;
    private String rawPassword;
    private String rawPasswordConfirmation;
}
