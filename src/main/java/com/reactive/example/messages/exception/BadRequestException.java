package com.reactive.example.messages.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super("BAD_REQUEST");
    }

    public BadRequestException(String reason) {
        super(reason);
    }

    public BadRequestException(String reason, Exception cause) {
        super(reason, cause);
    }
}
