package com.example.demo.Security.Utilities.PasswordRecovery.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordRecovery {
    private String email;
    private String rawPassword;
    private String rawPasswordConfirmation;
}
