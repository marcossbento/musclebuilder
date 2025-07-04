package com.musclebuilder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    // Construtor com mensagem e causa (útil para encadear exceções)
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
