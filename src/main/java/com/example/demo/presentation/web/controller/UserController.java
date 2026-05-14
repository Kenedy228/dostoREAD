package com.example.demo.presentation.web.controller;

import com.example.demo.presentation.web.form.UserCreateForm;
import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.presentation.web.validator.CreateUserValidator;
import com.example.demo.presentation.web.viewmodel.AccountViewModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class UserController {
    @Autowired
    private final UserApplicationService userService;
    @Autowired
    private final CreateUserValidator userValidator;

    @GetMapping("/admin-panel/accounts/view")
    public String getAllAccounts(Model model) {
        model.addAttribute("accounts", userService.findAdminAccounts());

        return "admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel/accounts/disable")
    public String disableAccount(@RequestParam int userID) {
        AccountViewModel user = userService.disableAccount(userID);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (user.username().equals(username)) {
            return "redirect:/logout";
        }

        return "redirect:/admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel/accounts/enable")
    public String enableAccount(@RequestParam int userID) {
        userService.enableAccount(userID);

        return "redirect:/admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel/accounts/create")
    public String getCreateForm(Model model) {
        model.addAttribute("userCreateDTO", new UserCreateForm());

        return "admin-panel/accounts/create";
    }

    @PostMapping("/admin-panel/accounts/create")
    public String createAdmin(@ModelAttribute UserCreateForm userCreateDTO, Model model, BindingResult bindingResult) {
        userValidator.validate(userCreateDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDTO", userCreateDTO);
            return "admin-panel/accounts/create";
        }

        userService.create(userCreateDTO.getUsername(), null, userCreateDTO.getRawPassword(), "ADMIN");

        return "redirect:/admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel")
    public String adminPanel() {
        return "admin-panel/adminPanel";
    }
}
