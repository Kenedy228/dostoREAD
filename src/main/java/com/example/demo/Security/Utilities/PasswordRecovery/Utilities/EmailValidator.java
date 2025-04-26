package com.example.demo.Security.Utilities.PasswordRecovery.Utilities;

import com.example.demo.Security.User.Service.UserService;
import com.example.demo.Security.Utilities.PasswordRecovery.Model.PasswordRecovery;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class EmailValidator implements Validator {

    @Autowired
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordRecovery.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.required", "Введите адрес электронной почты");

        if (errors.hasErrors()) {
            return;
        }

        PasswordRecovery passwordRecovery = (PasswordRecovery) target;

        if (!passwordRecovery.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            errors.rejectValue("email", "notEmail", "Введите корректный адрес электронной почты");
        } else if (userService.findUserByEmail(passwordRecovery.getEmail()) == null) {
            errors.rejectValue("email", "emailAlreadyExists", "Пользователь с указанной почтой не существует");
        }
    }
}
