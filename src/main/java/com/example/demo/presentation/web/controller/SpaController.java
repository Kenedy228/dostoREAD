package com.example.demo.presentation.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    @GetMapping({
            "/",
            "/login",
            "/registration",
            "/registration/email-confirmation",
            "/password-recovery",
            "/password-recovery/email-confirmation",
            "/password-recovery/email-confirmation/change-password",
            "/all-books",
            "/book-info",
            "/read-book",
            "/user-panel",
            "/admin-panel",
            "/admin-panel/books/view",
            "/admin-panel/books/create",
            "/admin-panel/books/update",
            "/admin-panel/accounts/view",
            "/admin-panel/accounts/create"
    })
    public String app() {
        return "forward:/index.html";
    }
}
