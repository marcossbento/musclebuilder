package com.musclebuilder.exception;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {

        super(String.format("Falha no token [%s]: %s", token, message));
    }
}
