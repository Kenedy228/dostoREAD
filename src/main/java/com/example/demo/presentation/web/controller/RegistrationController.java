package com.example.demo.presentation.web.controller;

import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.presentation.web.form.ConfirmationCodeForm;
import com.example.demo.application.mail.service.ConfirmationCodeApplicationService;
import com.example.demo.presentation.web.validator.ConfirmationCodeValidator;
import com.example.demo.presentation.web.form.RegistrationForm;
import com.example.demo.presentation.web.validator.RegistrationValidator;
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
public class RegistrationController {

    @Autowired
    private final RegistrationValidator registrationValidator;
    @Autowired
    private final ConfirmationCodeApplicationService confirmationCodeService;
    @Autowired
    private final ConfirmationCodeValidator codeValidator;
    @Autowired
    private final UserApplicationService userService;

    @GetMapping("/registration")
    public String getRegistration(Model model) {
        model.addAttribute("registration", new RegistrationForm());

        return "security/registration/registration";
    }

    @PostMapping("/registration")
    public String postRegistration(@ModelAttribute RegistrationForm registration, Model model, BindingResult bindingResult, HttpSession session) {
        registrationValidator.validate(registration, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("registration", registration);
            return "security/registration/registration";
        }

        session.setAttribute("email", registration.getEmail());
        session.setAttribute("username", registration.getUsername());
        session.setAttribute("rawPassword", registration.getRawPassword());

        return "redirect:/registration/email-confirmation";
    }

    @GetMapping("/registration/email-confirmation")
    public String getEmailConfirmation(Model model, HttpSession session) {
        if (!confirmationCodeService.isInfoProvided(session)) {
            return "redirect:/registration";
        }

        confirmationCodeService.send(session.getAttribute("email").toString());

        model.addAttribute("confirmationCodeDto", new ConfirmationCodeForm());

        return "security/registration/emailConfirmation";
    }

    @PostMapping("/registration/email-confirmation")
    public String postEmailConfirmation(@ModelAttribute ConfirmationCodeForm confirmationCodeDto, Model model, BindingResult bindingResult, HttpSession session) {
        if (!confirmationCodeService.isInfoProvided(session)) {
            return "redirect:/registration";
        }

        confirmationCodeDto.setExpectedCode(confirmationCodeService.getCodeByEmail(session.getAttribute("email").toString()));
        codeValidator.validate(confirmationCodeDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("confirmationCodeDto", confirmationCodeDto);
            return "security/registration/emailConfirmation";
        }

        userService.create(session.getAttribute("username").toString(), session.getAttribute("email").toString(), session.getAttribute("rawPassword").toString(), "USER");

        session.invalidate();

        return "redirect:/";
    }
}
