package com.example.demo.Security.Utilities.PasswordRecovery.Controller;

import com.example.demo.Security.User.Service.UserService;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.DTO.ConfirmationCodeDto;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.Service.ConfirmationCodeService;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities.Validator.ConfirmationCodeValidator;
import com.example.demo.Security.Utilities.PasswordRecovery.Model.PasswordRecovery;
import com.example.demo.Security.Utilities.PasswordRecovery.Utilities.EmailValidator;
import com.example.demo.Security.Utilities.PasswordRecovery.Utilities.PasswordValidator;
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
    private final EmailValidator emailValidator;
    @Autowired
    private final ConfirmationCodeService confirmationCodeService;
    @Autowired
    private final ConfirmationCodeValidator codeValidator;
    @Autowired
    private final PasswordValidator passwordValidator;
    @Autowired
    private final UserService userService;

    @GetMapping("/password-recovery")
    public String getPasswordRecovery(Model model) {
        model.addAttribute("passwordRecovery", new PasswordRecovery());
        return "security/password-recovery/index";
    }

    @PostMapping("/password-recovery")
    public String postPasswordRecovery(@ModelAttribute PasswordRecovery passwordRecovery, Model model, BindingResult bindingResult, HttpSession session) {
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

        model.addAttribute("confirmationCodeDto", new ConfirmationCodeDto());

        return "security/password-recovery/email-confirmation";
    }

    @PostMapping("/password-recovery/email-confirmation")
    public String postEmailConfirmation(@ModelAttribute ConfirmationCodeDto confirmationCodeDto, Model model, BindingResult bindingResult, HttpSession session) {
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

        model.addAttribute("passwordRecovery", new PasswordRecovery());

        return "security/password-recovery/change-password";
    }

    @PostMapping("/password-recovery/email-confirmation/change-password")
    public String postChangePassword(@ModelAttribute PasswordRecovery passwordRecovery, Model model, BindingResult bindingResult, HttpSession session) {
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
