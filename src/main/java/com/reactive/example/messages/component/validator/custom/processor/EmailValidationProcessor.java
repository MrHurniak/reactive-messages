package com.reactive.example.messages.component.validator.custom.processor;

import com.reactive.example.messages.component.validator.custom.annotation.Email;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class EmailValidationProcessor implements ConstraintValidator<Email, String> {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();
    private boolean isNullable;

    @Override
    public void initialize(Email constraintAnnotation) {
        this.isNullable = constraintAnnotation.isNullable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (Objects.isNull(value) == isNullable) || EMAIL_VALIDATOR.isValid(value);
    }
}
