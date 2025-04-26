package com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CodeGenerator {
    public String generate() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
