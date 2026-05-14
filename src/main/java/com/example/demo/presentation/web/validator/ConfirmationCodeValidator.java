package com.example.demo.presentation.web.validator;

import com.example.demo.presentation.web.form.ConfirmationCodeForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ConfirmationCodeValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ConfirmationCodeForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "enteredCode", "enteredCode.required", "Введите код подтверждения");

        if (errors.hasErrors()) {
            return;
        }

        ConfirmationCodeForm confirmationCodeDto = (ConfirmationCodeForm) target;

        if (!confirmationCodeDto.getEnteredCode().equals(confirmationCodeDto.getExpectedCode())) {
            errors.rejectValue("enteredCode", "enteredCodeMismatch", "Неверный код");
        }
    }
}
