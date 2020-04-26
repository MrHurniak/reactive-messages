package com.reactive.example.messages.validator.impl;

import com.reactive.example.messages.exception.BadRequestException;
import com.reactive.example.messages.exception.FieldValidationException;
import com.reactive.example.messages.validator.AbstractValidationHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class SpringValidationHandler extends AbstractValidationHandler {

    public SpringValidationHandler(@Qualifier("defaultValidator") Validator validator) {
        super(validator);
    }

    @Override
    protected RuntimeException onValidationError(Errors errors, ServerRequest request) {
        if (!errors.getFieldErrors().isEmpty()) {
            FieldValidationException validationException = new FieldValidationException();
            for (FieldError fieldError : errors.getFieldErrors()) {
                validationException.addField(fieldError.getField(), fieldError.getDefaultMessage());
            }
            throw validationException;
        }
        return new BadRequestException(errors.getAllErrors().toString());
    }
}
