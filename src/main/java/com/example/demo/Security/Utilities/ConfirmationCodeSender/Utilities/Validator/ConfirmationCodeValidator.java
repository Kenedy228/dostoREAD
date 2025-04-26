package com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities.Validator;

import com.example.demo.Security.Utilities.ConfirmationCodeSender.DTO.ConfirmationCodeDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ConfirmationCodeValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ConfirmationCodeDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "enteredCode", "enteredCode.required", "Введите код подтверждения");

        if (errors.hasErrors()) {
            return;
        }

        ConfirmationCodeDto confirmationCodeDto = (ConfirmationCodeDto) target;

        if (!confirmationCodeDto.getEnteredCode().equals(confirmationCodeDto.getExpectedCode())) {
            errors.rejectValue("enteredCode", "enteredCodeMismatch", "Неверный код");
        }
    }
}
