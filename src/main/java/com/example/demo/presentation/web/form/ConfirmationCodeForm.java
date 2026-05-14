package com.example.demo.presentation.web.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfirmationCodeForm {
    private String enteredCode;
    private String expectedCode;
}
