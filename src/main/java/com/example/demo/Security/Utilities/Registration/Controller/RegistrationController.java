package com.example.demo.Security.Utilities.Registration.Controller;

import com.example.demo.Security.User.Service.UserService;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.DTO.ConfirmationCodeDto;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.Service.ConfirmationCodeService;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities.Validator.ConfirmationCodeValidator;
import com.example.demo.Security.Utilities.Registration.Model.Registration;
import com.example.demo.Security.Utilities.Registration.Utilities.Validator.RegistrationValidator;
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
    private final ConfirmationCodeService confirmationCodeService;
    @Autowired
    private final ConfirmationCodeValidator codeValidator;
    @Autowired
    private final UserService userService;

    @GetMapping("/registration")
    public String getRegistration(Model model) {
        model.addAttribute("registration", new Registration());

        return "security/registration/registration";
    }

    @PostMapping("/registration")
    public String postRegistration(@ModelAttribute Registration registration, Model model, BindingResult bindingResult, HttpSession session) {
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

        model.addAttribute("confirmationCodeDto", new ConfirmationCodeDto());

        return "security/registration/emailConfirmation";
    }

    @PostMapping("/registration/email-confirmation")
    public String postEmailConfirmation(@ModelAttribute ConfirmationCodeDto confirmationCodeDto, Model model, BindingResult bindingResult, HttpSession session) {
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
