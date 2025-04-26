package com.example.demo.Security.Utilities.ConfirmationCodeSender.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfirmationCodeDto {
    private String enteredCode;
    private String expectedCode;
}
