package com.reactive.example.messages.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends RuntimeException {

    public AuthorizationException() {
        super("INVALID_CREDENTIALS");
    }
}
