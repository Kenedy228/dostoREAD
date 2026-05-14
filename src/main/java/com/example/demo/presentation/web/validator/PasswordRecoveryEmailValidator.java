package com.example.demo.presentation.web.validator;

import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.presentation.web.form.PasswordRecoveryForm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class PasswordRecoveryEmailValidator implements Validator {

    @Autowired
    private final UserApplicationService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordRecoveryForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.required", "Введите адрес электронной почты");

        if (errors.hasErrors()) {
            return;
        }

        PasswordRecoveryForm passwordRecovery = (PasswordRecoveryForm) target;

        if (!passwordRecovery.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            errors.rejectValue("email", "notEmail", "Введите корректный адрес электронной почты");
        } else if (userService.findUserByEmail(passwordRecovery.getEmail()) == null) {
            errors.rejectValue("email", "emailAlreadyExists", "Пользователь с указанной почтой не существует");
        }
    }
}
