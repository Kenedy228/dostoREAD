package com.example.demo.presentation.web.validator;

import com.example.demo.presentation.web.form.UserCreateForm;
import com.example.demo.application.user.service.UserApplicationService;
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
    private final UserApplicationService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserCreateForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required", "Заполните поле имя пользователя");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rawPassword", "rawPassword.required", "Заполните поля пароль");

        if (errors.hasErrors()) {
            return;
        }

        UserCreateForm userCreateDTO = (UserCreateForm) target;

        if (userService.findAdminByUsername(userCreateDTO.getUsername()) != null) {
            errors.rejectValue("username", "userAlreadyExists", "Администратор с указанным именем уже существует");
        }

        if (!userCreateDTO.getRawPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,14}$")) {
            errors.rejectValue("rawPassword", "rawPasswordMaskMismatch", "Пароль не отвечает требованиям сложности");
        }
    }
}
