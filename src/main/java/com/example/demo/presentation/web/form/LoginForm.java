package com.example.demo.presentation.web.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginForm {
    private String username;
    private String rawPassword;
}
