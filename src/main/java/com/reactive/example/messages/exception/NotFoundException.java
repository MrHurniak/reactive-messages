package com.reactive.example.messages.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends BadRequestException {

    public NotFoundException(String reason) {
        super(reason);
    }

    public NotFoundException(String reason, Exception cause) {
        super(reason, cause);
    }

}
