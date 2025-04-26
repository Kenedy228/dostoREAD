package com.example.demo.Security.Utilities.Registration.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Registration {
    private String username;
    private String rawPassword;
    private String rawPasswordConfirmation;
    private String email;
}