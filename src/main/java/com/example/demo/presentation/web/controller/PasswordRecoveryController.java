package com.example.demo.presentation.web.controller;

import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.presentation.web.form.ConfirmationCodeForm;
import com.example.demo.application.mail.service.ConfirmationCodeApplicationService;
import com.example.demo.presentation.web.validator.ConfirmationCodeValidator;
import com.example.demo.presentation.web.form.PasswordRecoveryForm;
import com.example.demo.presentation.web.validator.PasswordRecoveryEmailValidator;
import com.example.demo.presentation.web.validator.PasswordRecoveryPasswordValidator;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class PasswordRecoveryController {

    @Autowired
    private final PasswordRecoveryEmailValidator emailValidator;
    @Autowired
    private final ConfirmationCodeApplicationService confirmationCodeService;
    @Autowired
    private final ConfirmationCodeValidator codeValidator;
    @Autowired
    private final PasswordRecoveryPasswordValidator passwordValidator;
    @Autowired
    private final UserApplicationService userService;

    @GetMapping("/password-recovery")
    public String getPasswordRecovery(Model model) {
        model.addAttribute("passwordRecovery", new PasswordRecoveryForm());
        return "security/password-recovery/index";
    }

    @PostMapping("/password-recovery")
    public String postPasswordRecovery(@ModelAttribute PasswordRecoveryForm passwordRecovery, Model model, BindingResult bindingResult, HttpSession session) {
        emailValidator.validate(passwordRecovery, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordRecovery", passwordRecovery);
            return "security/password-recovery/index";
        }

        session.setAttribute("reset-email", passwordRecovery.getEmail());

        return "redirect:/password-recovery/email-confirmation";
    }

    @GetMapping("/password-recovery/email-confirmation")
    public String getEmailConfirmation(Model model, HttpSession session) {
        if (session.getAttribute("reset-email") == null) {
            return "redirect:/password-recovery";
        }

        confirmationCodeService.send(session.getAttribute("reset-email").toString());

        model.addAttribute("confirmationCodeDto", new ConfirmationCodeForm());

        return "security/password-recovery/email-confirmation";
    }

    @PostMapping("/password-recovery/email-confirmation")
    public String postEmailConfirmation(@ModelAttribute ConfirmationCodeForm confirmationCodeDto, Model model, BindingResult bindingResult, HttpSession session) {
        if (session.getAttribute("reset-email") == null) {
            return "redirect:/password-recovery";
        }

        confirmationCodeDto.setExpectedCode(confirmationCodeService.getCodeByEmail(session.getAttribute("reset-email").toString()));

        codeValidator.validate(confirmationCodeDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("confirmationCodeDto", confirmationCodeDto);
            return "security/password-recovery/email-confirmation";
        }

        session.setAttribute("code", true);

        return "redirect:/password-recovery/email-confirmation/change-password";
    }

    @GetMapping("/password-recovery/email-confirmation/change-password")
    public String getChangePassword(Model model, HttpSession session) {
        if (session.getAttribute("reset-email") == null && session.getAttribute("code") == null) {
            return "redirect:/password-recovery";
        }

        model.addAttribute("passwordRecovery", new PasswordRecoveryForm());

        return "security/password-recovery/change-password";
    }

    @PostMapping("/password-recovery/email-confirmation/change-password")
    public String postChangePassword(@ModelAttribute PasswordRecoveryForm passwordRecovery, Model model, BindingResult bindingResult, HttpSession session) {
        if (session.getAttribute("reset-email") == null && session.getAttribute("code") == null) {
            return "redirect:/password-recovery";
        }

        passwordValidator.validate(passwordRecovery, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordRecovery", passwordRecovery);
            return "security/password-recovery/change-password";
        }

        userService.updatePasswordByEmail(session.getAttribute("reset-email").toString(), passwordRecovery.getRawPassword());

        session.invalidate();

        return "redirect:/";
    }
}
