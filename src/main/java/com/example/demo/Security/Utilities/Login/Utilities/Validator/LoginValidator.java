package com.example.demo.Security.Utilities.Login.Utilities.Validator;

import com.example.demo.Security.User.Model.User;
import com.example.demo.Security.User.Service.UserService;
import com.example.demo.Security.Utilities.Login.Model.Login;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class LoginValidator implements Validator {

    @Autowired
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Login.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required", "Введите имя пользователя");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPassword", "password.required", "Введите пароль");

        if (errors.hasErrors()) {
            return;
        }

        Login login = (Login) target;

        User user = userService.findUserByUsername(login.getUsername());

        if (user == null) {
            errors.reject("userNotFound", "Пользователя с указанными данными не найдено");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(login.getRawPassword(), user.getPassword())) {
            errors.reject("userNotFound", "Пользователя с указанными данными не найдено");
            return;
        }

        if (!user.isEnabled()) {
            errors.reject("accountIsDisableb", "Аккаунт заблокирован");
            return;
        }
    }
}