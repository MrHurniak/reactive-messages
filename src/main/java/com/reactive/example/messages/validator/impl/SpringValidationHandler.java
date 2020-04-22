package com.reactive.example.messages.validator.impl;

import com.reactive.example.messages.validator.AbstractValidationHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class SpringValidationHandler extends AbstractValidationHandler {

    public SpringValidationHandler(Validator validator) {
        super(validator);
    }

    @Override
    protected Exception onValidationError(Errors errors, ServerRequest request) {
        return new RuntimeException("Validation error" + errors.getAllErrors());
    }
}
