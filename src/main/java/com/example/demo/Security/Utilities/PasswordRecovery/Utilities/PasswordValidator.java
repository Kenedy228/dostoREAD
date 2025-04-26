package com.example.demo.Security.Utilities.PasswordRecovery.Utilities;

import com.example.demo.Security.Utilities.PasswordRecovery.Model.PasswordRecovery;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class PasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordRecovery.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPassword", "rawPassword.required", "Заполните пароль");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPasswordConfirmation", "rawPasswordConfirmation.required", "Заполните подтверждение пароля");

        if (errors.hasErrors()) {
            return;
        }

        PasswordRecovery passwordRecovery = (PasswordRecovery) target;

        if (!passwordRecovery.getRawPassword().equals(passwordRecovery.getRawPasswordConfirmation())) {
            errors.rejectValue("rawPasswordConfirmation", "rawPasswordConfirmationMismatch", "Введенный пароль и подтверждение пароля не совпадают");
        } else if (!passwordRecovery.getRawPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,14}$")) {
            errors.rejectValue("rawPassword", "rawPasswordMaskMismatch", "Пароль не отвечает требованиям сложности");
        }
    }
}
