package com.example.demo.Security.Utilities.Registration.Utilities.Validator;

import com.example.demo.Security.User.Repository.UserRepository;
import com.example.demo.Security.User.Service.UserService;
import com.example.demo.Security.Utilities.Registration.Model.Registration;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class RegistrationValidator implements Validator {

    @Autowired
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Registration.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required", "Заполните поле имя пользователя");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.required", "Заполните адрес электронной почты");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPassword", "rawPassword.required", "Заполните пароль");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPasswordConfirmation", "rawPasswordConfirmation.required", "Заполните подтверждение пароля");

        if (errors.hasErrors()) {
            return;
        }

        Registration registration = (Registration) target;

        if (userService.findUserByUsername(registration.getUsername()) != null) {
            errors.rejectValue("username", "usernameAlreadyExists", "Пользователь с таким именем уже существует");
        }

        if (!registration.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
           errors.rejectValue("email", "notEmail", "Введите корректный адрес электронной почты");
        } else if (userService.findUserByEmail(registration.getEmail()) != null) {
            errors.rejectValue("email", "emailAlreadyExists", "Пользователь с такой почтой уже существует");
        }

        if (!registration.getRawPassword().equals(registration.getRawPasswordConfirmation())) {
            errors.rejectValue("rawPasswordConfirmation", "rawPasswordConfirmationMismatch", "Введенный пароль и подтверждение пароля не совпадают");
        } else if (!registration.getRawPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,14}$")) {
            errors.rejectValue("rawPassword", "rawPasswordMaskMismatch", "Пароль не отвечает требованиям сложности");
        }
    }
}
