package com.reactive.example.messages.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class FieldValidationException extends BadRequestException {

    private final Map<String, String> fieldNamesToErrors = new HashMap<>();

    public FieldValidationException(String fieldName, String validationMassage) {
        super("VALIDATION_ERROR");
        fieldNamesToErrors.put(fieldName, validationMassage);
    }

    public FieldValidationException() {
        super("VALIDATION_ERROR");
    }

    public FieldValidationException addField(String fieldName, String validationMessage) {
        fieldNamesToErrors.put(fieldName, validationMessage);
        return this;
    }

    @Override
    public String getMessage() {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (Map.Entry<String, String> fieldNameToErrorMessage : fieldNamesToErrors.entrySet()) {
            stringJoiner.add("'" + fieldNameToErrorMessage.getKey() + "':'" + fieldNameToErrorMessage.getValue() + "'");
        }
        return "'Validation Error': {" + stringJoiner.toString() + "}";
    }
}
