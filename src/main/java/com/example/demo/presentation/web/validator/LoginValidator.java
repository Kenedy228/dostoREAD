package com.example.demo.presentation.web.validator;

import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.presentation.web.form.LoginForm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class LoginValidator implements Validator {

    @Autowired
    private final UserApplicationService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required", "Введите имя пользователя");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPassword", "password.required", "Введите пароль");

        if (errors.hasErrors()) {
            return;
        }

        LoginForm login = (LoginForm) target;

        if (!userService.existsByUsername(login.getUsername())) {
            errors.reject("userNotFound", "Пользователя с указанными данными не найдено");
            return;
        }

        if (!userService.canLogin(login.getUsername(), login.getRawPassword())) {
            if (!userService.isEnabled(login.getUsername())) {
                errors.reject("accountIsDisableb", "Аккаунт заблокирован");
                return;
            }

            errors.reject("userNotFound", "Пользователя с указанными данными не найдено");
        }
    }
}
