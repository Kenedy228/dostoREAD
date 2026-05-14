package com.example.demo.presentation.web.validator;

import com.example.demo.presentation.web.form.PasswordRecoveryForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class PasswordRecoveryPasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordRecoveryForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPassword", "rawPassword.required", "Заполните пароль");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPasswordConfirmation", "rawPasswordConfirmation.required", "Заполните подтверждение пароля");

        if (errors.hasErrors()) {
            return;
        }

        PasswordRecoveryForm passwordRecovery = (PasswordRecoveryForm) target;

        if (!passwordRecovery.getRawPassword().equals(passwordRecovery.getRawPasswordConfirmation())) {
            errors.rejectValue("rawPasswordConfirmation", "rawPasswordConfirmationMismatch", "Введенный пароль и подтверждение пароля не совпадают");
        } else if (!passwordRecovery.getRawPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,14}$")) {
            errors.rejectValue("rawPassword", "rawPasswordMaskMismatch", "Пароль не отвечает требованиям сложности");
        }
    }
}
