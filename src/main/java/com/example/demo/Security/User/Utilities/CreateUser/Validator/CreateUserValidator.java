package com.example.demo.Security.User.Utilities.CreateUser.Validator;

import com.example.demo.Security.User.DTO.UserCreateDTO;
import com.example.demo.Security.User.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class CreateUserValidator implements Validator {

    @Autowired
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserCreateDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required", "Заполните поле имя пользователя");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPassword", "rawPassword.required", "Заполните поля пароль");

        if (errors.hasErrors()) {
            return;
        }

        UserCreateDTO userCreateDTO = (UserCreateDTO) target;

        if (userService.findAdminByUsername(userCreateDTO.getUsername()) != null) {
            errors.rejectValue("username", "userAlreadyExists", "Администратор с указанным именем уже существует");
        }

        if (!userCreateDTO.getRawPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,14}$")) {
            errors.rejectValue("rawPassword", "rawPasswordMaskMismatch", "Пароль не отвечает требованиям сложности");
        }
    }
}
